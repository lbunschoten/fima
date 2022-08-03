package fima.services.subscription

import fima.services.subscription.SubscriptionDtos.*
import fima.services.subscription.repository.PostgresSubscriptionRepository
import fima.services.transaction.TransactionService.ZioTransactionService.TransactionServiceClient
import sttp.tapir.PublicEndpoint
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.http4s.Http4sServerOptions
import sttp.tapir.server.http4s.ztapir.*
import sttp.tapir.server.interceptor.cors.{CORSConfig, CORSInterceptor}
import sttp.tapir.ztapir.*
import zio.*
import zio.interop.catz.*
import zio.interop.catz.implicits.*

import java.util.UUID

object SubscriptionApi {

  lazy val live: ZLayer[PostgresSubscriptionRepository & TransactionServiceClient.Service, Any, SubscriptionApi] = {
    ZLayer {
      for {
        subscriptionRepository <- ZIO.service[PostgresSubscriptionRepository]
        transactionService <- ZIO.service[TransactionServiceClient.Service]
      } yield {
        new SubscriptionApi(subscriptionRepository, transactionService)
      }
    }
  }
}

class SubscriptionApi(subscriptionRepository: PostgresSubscriptionRepository, transactionService: TransactionServiceClient.Service) {

  private val getSubscriptionByIdEndpoint: PublicEndpoint[UUID, String, GetSubscriptionResponseDto, Any] = {
    endpoint
      .get
      .in("subscription" / path[UUID]("id"))
      .out(jsonBody[GetSubscriptionResponseDto])
      .errorOut(plainBody[String])
  }

  private def getSubscriptionById: ZServerEndpoint[Any, Any] = getSubscriptionByIdEndpoint.zServerLogic { subscriptionId: UUID =>
    for {
      subscriptionOpt <- subscriptionRepository.findById(subscriptionId).mapError(e => s"Failed to retrieve subscription: ${e.getMessage}")
      subscription <- ZIO.fromOption(subscriptionOpt).orElseFail(s"Could not find subscription $subscriptionId")
      searchTransactionsResponse <- transactionService
        .searchTransactions(subscription.query.toProto)
        .mapError(e => s"Failed to retrieve transactions for subscription $subscriptionId: ${e.asException().getMessage}")
    } yield {
      GetSubscriptionResponseDto(
        subscription = Option(SubscriptionDto(subscription.id.toString, subscription.name, subscription.recurrence.name.toUpperCase())),
        transactions = searchTransactionsResponse.transactions.map(TransactionDto.fromDomain)
      )
    }
  }

  private val getSubscriptionsEndpoint: PublicEndpoint[Unit, String, Seq[SubscriptionDto], Any] = {
    endpoint
      .get
      .in("subscriptions")
      .out(jsonBody[Seq[SubscriptionDto]])
      .errorOut(plainBody[String])
  }

  private def getSubscriptions: ZServerEndpoint[Any, Any] = getSubscriptionsEndpoint.zServerLogic { _ =>
    subscriptionRepository
      .findAll()
      .mapBoth(
        e => s"Could not retrieve subscriptions: ${e.getMessage}",
        s => s.map { s => SubscriptionDto(s.id.toString, s.name, s.recurrence.name.toUpperCase()) }
      )
  }

  val corsInterceptor: CORSInterceptor[Task] = CORSInterceptor.customOrThrow[Task](CORSConfig.default)
  val serverOptions: Http4sServerOptions[Task] = Http4sServerOptions
    .customiseInterceptors[Task]
    .corsInterceptor(corsInterceptor)
    .options

  val routes = ZHttp4sServerInterpreter(serverOptions).from(List(
    getSubscriptionById,
    getSubscriptions
  )).toRoutes
}
