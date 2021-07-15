package fima.services.investment

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import cats.effect._
import doobie._
import doobie.hikari.HikariTransactor
import fima.services.investment.InvestmentService.InvestmentServiceGrpc.InvestmentService
import fima.services.investment.market.MarketValueUpdater
import fima.services.investment.market.alpha_vantage.AlphaVantageApi
import fima.services.investment.repository.StockRepository
import io.grpc.Server
import io.grpc.netty.NettyServerBuilder

import java.util.concurrent.{Executors, ScheduledThreadPoolExecutor, TimeUnit}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.language.existentials
import scala.util.{Failure, Success}


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
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "api-system")
    val actorExcutionContext = system.executionContext

    val dbHost: String = Option(System.getenv("FIMA_POSTGRES_DB_SERVICE_HOST")).getOrElse("localhost")
    val dbPort: String = Option(System.getenv("FIMA_POSTGRES_DB_SERVICE_PORT")).getOrElse("3306")
    val dbPassword: String = Option(System.getenv("DB_PASSWORD")).getOrElse("root123")
    val alphaVantageApiBaseUrl: String = System.getenv("ALPHA_VANTAGE_API_BASE_URL")
    val alphaVantageApiKey: String = System.getenv("ALPHA_VANTAGE_API_KEY")

    val transactor = startDbTransactor(dbHost, dbPort, dbPassword)

    val stockRepository = new StockRepository()
    val api = new InvestmentServiceApi(stockRepository, transactor)
    val stockApi = new AlphaVantageApi(alphaVantageApiBaseUrl, alphaVantageApiKey)
    val apiServer = Http()
      .newServerAt("0.0.0.0", 8080)
      .bind(api.routes)

    apiServer.onComplete {
      case Success(v) => println(s"HTTP Server started at ${v.localAddress}")
      case Failure(e) => println(s"Failed to start HTTP server: ${e.getMessage}")
    }

    server = NettyServerBuilder
      .forPort(InvestmentServiceServer.port)
      .addService(InvestmentService.bindService(new InvestmentServiceImpl(stockRepository, transactor), executionContext))
      .build.start

    val stockPriceCollector = new MarketValueUpdater(stockApi, stockRepository, transactor)
    val executor = new ScheduledThreadPoolExecutor(1)
    executor.scheduleWithFixedDelay(() => stockPriceCollector.updateMarketValues(), 0L, 1, TimeUnit.HOURS)

    println("Server started, listening on " + InvestmentServiceServer.port)
    sys.addShutdownHook {
      System.err.println("*** shutting down gRPC server since JVM is shutting down")
      stop()
      System.err.println("*** server shut down")
      apiServer
        .flatMap(_.unbind())(actorExcutionContext) // trigger unbinding from the port
        .onComplete(_ => system.terminate())(actorExcutionContext)
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