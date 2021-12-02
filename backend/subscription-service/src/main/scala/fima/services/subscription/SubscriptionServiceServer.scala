package fima.services.subscription

import cats.effect._
import doobie._
import doobie.hikari.HikariTransactor
import fima.services.subscription.SubscriptionService.SubscriptionServiceGrpc.SubscriptionService
import fima.services.transaction.TransactionService.TransactionServiceGrpc
import io.grpc.Server
import io.grpc.netty.{NettyChannelBuilder, NettyServerBuilder}

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.language.existentials


object SubscriptionServiceServer {

  private val port = 9997

  def main(args: Array[String]): Unit = {
    new SubscriptionServiceServer(ExecutionContext.global).blockUntilShutdown()
  }

}

class SubscriptionServiceServer(private val executionContext: ExecutionContext) {

  private implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))
  private val server: Server = start()

  private def start(): Server = {
    val dbHost: String = Option(System.getenv("FIMA_POSTGRES_DB_SERVICE_HOST")).getOrElse("localhost")
    val dbPort: String = Option(System.getenv("FIMA_POSTGRES_DB_SERVICE_PORT")).getOrElse("3306")
    val dbPassword: String = Option(System.getenv("DB_PASSWORD")).getOrElse("root123")
    val transactionServiceHost = System.getenv("TRANSACTION_SERVICE_SERVICE_HOST")
    val transactionServicePort = System.getenv("TRANSACTION_SERVICE_SERVICE_PORT").toInt

    val transactor = startDbTransactor(dbHost, dbPort, dbPassword)

    val channel = NettyChannelBuilder.forAddress(transactionServiceHost, transactionServicePort).usePlaintext().build()
    val transactionService: TransactionServiceGrpc.TransactionServiceStub = TransactionServiceGrpc.stub(channel)

    val subscriptionRepository = new SubscriptionRepository()
    val server = NettyServerBuilder
      .forPort(SubscriptionServiceServer.port)
      .addService(SubscriptionService.bindService(new SubscriptionServiceImpl(subscriptionRepository, transactionService, transactor), executionContext))
      .build.start

    println("Server started, listening on " + SubscriptionServiceServer.port)
    sys.addShutdownHook {
      System.err.println("*** shutting down gRPC server since JVM is shutting down")
      stop()
      System.err.println("*** server shut down")
    }

    server
  }

  private def startDbTransactor(dbHost: String, dbPort: String, dbPassword: String): Resource[IO, HikariTransactor[IO]] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

    for {
      connectExecutionContext <- ExecutionContexts.fixedThreadPool[IO](32)
      blockingExecutionContext <- Blocker[IO]
      transactor <- HikariTransactor.newHikariTransactor[IO](
        driverClassName = "org.postgresql.Driver",
        url = s"jdbc:postgresql://$dbHost:$dbPort/fima?createDatabaseIfNotExist=true&currentSchema=transaction",
        user = "root",
        pass = dbPassword,
        connectEC = connectExecutionContext,
        blocker = blockingExecutionContext
      )
    } yield transactor
  }

  private def stop(): Unit = {
    server.shutdown()
  }

  private def blockUntilShutdown(): Unit = {
    server.awaitTermination()
  }

}