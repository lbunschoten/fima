package fima.services.investment

import cats.effect._
import doobie._
import doobie.hikari.HikariTransactor
import fima.services.investment.InvestmentService.InvestmentServiceGrpc.InvestmentService
import io.grpc.Server
import io.grpc.netty.NettyServerBuilder

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.language.existentials


object InvestmentServiceServer {

  private val port = 9997

  def main(args: Array[String]): Unit = {
    val server = new InvestmentServiceServer(ExecutionContext.global)
    server.start()
    server.blockUntilShutdown()
  }

}

class InvestmentServiceServer(executionContext: ExecutionContext) {

  private[this] var server: Server = _

  private implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))

  private def start(): Unit = {
    val dbHost: String = Option(System.getenv("FIMA_POSTGRES_DB_SERVICE_HOST")).getOrElse("localhost")
    val dbPort: String = Option(System.getenv("FIMA_POSTGRES_DB_SERVICE_PORT")).getOrElse("3306")
    val dbPassword: String = Option(System.getenv("DB_PASSWORD")).getOrElse("root123")

    val transactor = startDbTransactor(dbHost, dbPort, dbPassword)

    val stockRepository = new StockRepository()
    server = NettyServerBuilder
      .forPort(InvestmentServiceServer.port)
      .addService(InvestmentService.bindService(new InvestmentServiceImpl(stockRepository, transactor), executionContext))
      .build.start

    println("Server started, listening on " + InvestmentServiceServer.port)
    sys.addShutdownHook {
      System.err.println("*** shutting down gRPC server since JVM is shutting down")
      stop()
      System.err.println("*** server shut down")
    }
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
    if (server != null) {
      server.shutdown()
    }
  }

  private def blockUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination()
    }
  }

}