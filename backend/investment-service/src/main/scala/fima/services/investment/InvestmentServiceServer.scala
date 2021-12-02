package fima.services.investment

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import cats.effect._
import doobie._
import doobie.hikari.HikariTransactor
import fima.services.investment.InvestmentService.InvestmentServiceFs2Grpc
import fima.services.investment.market.MarketValueUpdater
import fima.services.investment.market.alpha_vantage.AlphaVantageApi
import fima.services.investment.repository.{StockRepository, TransactionRepository}
import fs2.grpc.syntax.all.fs2GrpcSyntaxServerBuilder
import io.grpc.ServerServiceDefinition
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder

import java.util.concurrent.{Executors, ScheduledThreadPoolExecutor, TimeUnit}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.language.existentials
import scala.util.{Failure, Success}

object InvestmentServiceServer extends IOApp.Simple {

  private val port = 9997
  private val dbHost: String = Option(System.getenv("FIMA_POSTGRES_DB_SERVICE_HOST")).getOrElse("localhost")
  private val dbPort: String = Option(System.getenv("FIMA_POSTGRES_DB_SERVICE_PORT")).getOrElse("3306")
  private val dbPassword: String = Option(System.getenv("DB_PASSWORD")).getOrElse("root123")
  private val alphaVantageApiBaseUrl: String = System.getenv("ALPHA_VANTAGE_API_BASE_URL")
  private val alphaVantageApiKey: String = System.getenv("ALPHA_VANTAGE_API_KEY")
  private implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))

  override def run: IO[Unit] = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "api-system")

    val stockRepository = new StockRepository()
    val transactionRepository = new TransactionRepository()
    val resources = for {
      transactor <- startDbTransactor(dbHost, dbPort, dbPassword)
      investmentService = new InvestmentServiceImpl(stockRepository, transactor)(ec)
      service <- InvestmentServiceFs2Grpc.bindServiceResource(investmentService)
    } yield Resources(service, transactor)

    resources.use(r => {
      val api = new InvestmentServiceApi(stockRepository, transactionRepository, r.transactor)(ec, runtime)
      val stockApi = new AlphaVantageApi(alphaVantageApiBaseUrl, alphaVantageApiKey)
      val apiServer = Http()
        .newServerAt("0.0.0.0", 8080)
        .bind(api.routes)

      apiServer.onComplete {
        case Success(v) => println(s"HTTP Server started at ${v.localAddress}")
        case Failure(e) => println(s"Failed to start HTTP server: ${e.getMessage}")
      }

      val stockPriceCollector = new MarketValueUpdater(stockApi, stockRepository, r.transactor)(runtime)
      val executor = new ScheduledThreadPoolExecutor(1)
      executor.scheduleWithFixedDelay(() => stockPriceCollector.updateMarketValues(), 0L, 1, TimeUnit.HOURS)

      NettyServerBuilder
        .forPort(port)
        .addService(r.service)
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
        connectEC = connectExecutionContext,
      )
    } yield transactor
  }

  case class Resources(service: ServerServiceDefinition, transactor: Transactor[IO])
}