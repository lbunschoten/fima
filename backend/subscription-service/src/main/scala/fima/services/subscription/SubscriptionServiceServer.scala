package fima.services.subscription

import cats.effect._
import doobie._
import doobie.hikari.HikariTransactor
import fima.services.subscription.SubscriptionService.SubscriptionServiceFs2Grpc
import fima.services.transaction.TransactionService.TransactionServiceFs2Grpc
import fs2.grpc.syntax.all._
import io.grpc.ServerServiceDefinition
import io.grpc.netty.shaded.io.grpc.netty.{NettyChannelBuilder, NettyServerBuilder}

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.language.existentials


object SubscriptionServiceServer extends IOApp.Simple {

  private val port = 9997
  private val dbHost: String = Option(System.getenv("FIMA_POSTGRES_DB_SERVICE_HOST")).getOrElse("localhost")
  private val dbPort: String = Option(System.getenv("FIMA_POSTGRES_DB_SERVICE_PORT")).getOrElse("3306")
  private val dbPassword: String = Option(System.getenv("DB_PASSWORD")).getOrElse("root123")
  private val transactionServiceHost = System.getenv("TRANSACTION_SERVICE_SERVICE_HOST")
  private val transactionServicePort = System.getenv("TRANSACTION_SERVICE_SERVICE_PORT").toInt
  private implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))

  override def run: IO[Unit] = {
    val subscriptionRepository = new SubscriptionRepository()
    val serviceDefition = for {
      transactor <- startDbTransactor(dbHost, dbPort, dbPassword)
      channel <- NettyChannelBuilder.forAddress(transactionServiceHost, transactionServicePort).usePlaintext().resource[IO]
      transactionService <- TransactionServiceFs2Grpc.stubResource(channel)(Async[IO])
      subscriptionService = new SubscriptionServiceImpl(subscriptionRepository, transactionService, transactor)(ec, runtime)
      serviceDefition <- SubscriptionServiceFs2Grpc.bindServiceResource(subscriptionService)
    } yield serviceDefition

    serviceDefition.use(service => {
      NettyServerBuilder
        .forPort(port)
        .addService(service)
        .resource[IO]
        .evalMap(server => IO(server.start()))
        .evalTap(server => IO.println(s"Server started, listening on ${server.getPort}"))
        .useForever
        .onCancel(IO.println("Server is shutting down!"))
    })
  }

  private def startDbTransactor(dbHost: String, dbPort: String, dbPassword: String): Resource[IO, HikariTransactor[IO]] = {
    for {
      connectExecutionContext <- ExecutionContexts.fixedThreadPool[IO](32)
      transactor <- HikariTransactor.newHikariTransactor[IO](
        driverClassName = "org.postgresql.Driver",
        url = s"jdbc:postgresql://$dbHost:$dbPort/fima?createDatabaseIfNotExist=true&currentSchema=transaction",
        user = "root",
        pass = dbPassword,
        connectEC = connectExecutionContext
      )
    } yield transactor
  }

  case class Resources(serviceDefinition: ServerServiceDefinition
  )
}