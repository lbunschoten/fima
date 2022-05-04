package fima.services.subscription

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import doobie.Transactor
import doobie.implicits.*
import fima.domain.subscription.SubscriptionDomain.{Recurrence, Subscription}
import fima.domain.transaction.TransactionDomain.Transaction
import fima.services.subscription.SubscriptionService.*
import fima.services.subscription.implicits.SubscriptionSearchQueryExt
import fima.services.subscription.repository.{SubscriptionRepository, SubscriptionSearchQuery}
import fima.services.transaction.TransactionService.*
import io.grpc.Metadata

import java.util.UUID
import scala.concurrent.ExecutionContext

class SubscriptionServiceImpl(subscriptionRepository: SubscriptionRepository,
                              transactionService: TransactionServiceFs2Grpc[IO, Metadata],
                              transactor: Transactor[IO])
                             (private implicit val ec: ExecutionContext,
                              private implicit val runtime: IORuntime) extends SubscriptionServiceFs2Grpc[IO, Metadata] {

  override def getSubscription(request: GetSubscriptionRequest, ctx: Metadata): IO[GetSubscriptionResponse] = {
    for {
      subscription <- subscriptionRepository.findById(UUID.fromString(request.id)).transact(transactor)
      searchTransactionsResponse <- subscription.map { s =>
        transactionService.searchTransactions(s.query.asSearchRequest, new Metadata()).map { _.transactions }
      }.getOrElse(IO(Seq.empty))
    } yield GetSubscriptionResponse(
      subscription = subscription.map { s => Subscription(s.id.toString, s.name, Recurrence.fromValue(s.recurrence.id)) },
      transactions = searchTransactionsResponse
    )
  }

  override def getSubscriptions(request: GetSubscriptionsRequest, ctx: Metadata): IO[GetSubscriptionsResponse] = {
    for {
      subscriptions <- subscriptionRepository.findAll().transact(transactor)
    } yield GetSubscriptionsResponse(
      subscriptions = subscriptions.map { s => Subscription(s.id.toString, s.name, Recurrence.MONTHLY) },
    )
  }
}

object implicits {
  implicit class SubscriptionSearchQueryExt(val subscriptionSearchQuery: SubscriptionSearchQuery) {
    def asSearchRequest: SearchTransactionsRequest = {
      SearchTransactionsRequest()
        .withFilters(
          subscriptionSearchQuery.filters.map { f =>
            SearchFilter(
              f.query.map { q => QueryStringFilter(q.queryString) },
              f.tags.map { t => TransactionTagFilter(t.key, t.value) }
            )
          }
        )
    }
  }
}
