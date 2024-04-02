package fima.services.subscription

import doobie.hikari.HikariTransactor
import fima.services.subscription.repository.PostgresSubscriptionRepository
import fima.services.transaction.TransactionService.ZioTransactionService
import io.grpc.netty.NettyChannelBuilder
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import scalapb.zio_grpc.ZManagedChannel
import zio.*
import zio.interop.catz.*
import zio.interop.catz.implicits.*

import java.lang
import scala.language.existentials


object SubscriptionServiceServer extends ZIOAppDefault {

  private val port = 9998
  private val dbHost: String = Option(lang.System.getenv("FIMA_POSTGRES_DB_SERVICE_HOST")).getOrElse("localhost")
  private val dbPort: String = Option(lang.System.getenv("FIMA_POSTGRES_DB_SERVICE_PORT")).getOrElse("3306")
  private val dbPassword: String = Option(lang.System.getenv("DB_PASSWORD")).getOrElse("root123")
  private val transactionServiceHost = Option(lang.System.getenv("TRANSACTION_SERVICE_SERVICE_HOST")).getOrElse("localhost")
  private val transactionServicePort = Option(lang.System.getenv("TRANSACTION_SERVICE_SERVICE_PORT")).getOrElse("9997").toInt

  override def run: ZIO[Scope, Any, Any] = {
    for {
      transactor <- startDbTransactor(dbHost, dbPort, dbPassword)
      app <- buildApp(transactor)
    } yield app
  }

  private def buildApp(transactor: ULayer[HikariTransactor[Task]]): ZIO[Scope, Any, Unit] = {
    val channel = ZManagedChannel(NettyChannelBuilder.forAddress(transactionServiceHost, transactionServicePort).usePlaintext())

    ZLayer
      .make[SubscriptionApi](
        SubscriptionApi.live,
        ZioTransactionService.TransactionServiceClient.live(channel).tap(_ => ZIO.logInfo("Started transaction service client")),
        transactor.tap(_ => ZIO.logInfo("Started DB transactor")),
        PostgresSubscriptionRepository.live,
      )
      .build
      .map(_.get[SubscriptionApi])
      .flatMap(runHttp)
  }

  private def runHttp(subscriptionApi: SubscriptionApi): Task[Unit] = {
    val httpApp: HttpApp[Task] = Router(
      "" -> subscriptionApi.routes
    ).orNotFound

    BlazeServerBuilder
      .apply[Task]
      .withoutBanner
      .bindHttp(port, "0.0.0.0")
      .withHttpApp(httpApp)
      .enableHttp2(true)
      .serve
      .compile[Task, Task, cats.effect.ExitCode]
      .drain
  }

  private def startDbTransactor(dbHost: String, dbPort: String, dbPassword: String): RIO[Scope, ULayer[HikariTransactor[Task]]] = {
    val connectExecutionContext = ZIO.descriptor.map(_.executor.asExecutionContext)

    for {
      ec <- connectExecutionContext
      transactor <- HikariTransactor.newHikariTransactor[Task](
        driverClassName = "org.postgresql.Driver",
        url = s"jdbc:postgresql://$dbHost:$dbPort/fima?createDatabaseIfNotExist=true&currentSchema=transaction",
        user = "root",
        pass = dbPassword,
        connectEC = ec
      ).toScopedZIO
    } yield ZLayer.succeed(transactor)
  }
}