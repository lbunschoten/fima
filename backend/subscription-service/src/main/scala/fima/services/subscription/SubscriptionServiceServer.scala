package fima.services.subscription

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import cats.effect.*
import doobie.*
import doobie.hikari.HikariTransactor
import fima.services.subscription.repository.{PostgresSubscriptionRepository, SubscriptionRepository}
import fima.services.transaction.TransactionService.TransactionServiceFs2Grpc
import fs2.grpc.syntax.all.*
import io.grpc.{Metadata, ServerServiceDefinition}
import io.grpc.netty.shaded.io.grpc.netty.{NettyChannelBuilder, NettyServerBuilder}

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.language.existentials
import scala.util.{Failure, Success}


object SubscriptionServiceServer extends IOApp.Simple {

  private val port = 9998
  private val dbHost: String = Option(System.getenv("FIMA_POSTGRES_DB_SERVICE_HOST")).getOrElse("localhost")
  private val dbPort: String = Option(System.getenv("FIMA_POSTGRES_DB_SERVICE_PORT")).getOrElse("3306")
  private val dbPassword: String = Option(System.getenv("DB_PASSWORD")).getOrElse("root123")
  private val transactionServiceHost = System.getenv("TRANSACTION_SERVICE_SERVICE_HOST")
  private val transactionServicePort = System.getenv("TRANSACTION_SERVICE_SERVICE_PORT").toInt
  private implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))

  override def run: IO[Unit] = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "api-system")

    val subscriptionRepository = new PostgresSubscriptionRepository()
    val resources = for {
      transactor <- startDbTransactor(dbHost, dbPort, dbPassword)
      channel <- NettyChannelBuilder.forAddress(transactionServiceHost, transactionServicePort).usePlaintext().resource[IO]
      transactionService <- TransactionServiceFs2Grpc.stubResource(channel)(Async[IO])
    } yield Resources(transactionService, transactor)

    resources.use(r => {
      val api = new SubscriptionServiceApi(subscriptionRepository, r.transactionServiceFs2Grpc, r.transactor)(ec, runtime)
      val apiServer: Future[Http.ServerBinding] = Http()
        .newServerAt("0.0.0.0", port)
        .bind(api.routes)

      apiServer.onComplete {
        case Success(v) => println(s"HTTP Server started at ${v.localAddress}")
        case Failure(e) => println(s"Failed to start HTTP server: ${e.getMessage}")
      }

      IO.fromFuture(IO.pure(apiServer.map(_ => ())))
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

  case class Resources(transactionServiceFs2Grpc: TransactionServiceFs2Grpc[IO, Metadata], transactor: Transactor[IO])
}