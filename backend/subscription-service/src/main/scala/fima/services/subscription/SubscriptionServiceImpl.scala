package fima.services.subscription

import cats.effect.{ContextShift, IO, Resource}
import doobie.implicits._
import cats.implicits._
import doobie.{ExecutionContexts, Transactor}
import fima.domain.subscription.SubscriptionDomain.{Recurrence, Subscription}
import fima.domain.transaction.TransactionDomain.Transaction
import fima.services.subscription
import fima.services.subscription.SubscriptionService.SubscriptionServiceGrpc.SubscriptionService
import fima.services.subscription.SubscriptionService.{GetSubscriptionRequest, GetSubscriptionResponse, GetSubscriptionsRequest, GetSubscriptionsResponse}
import fima.services.transaction.TransactionService.TransactionServiceGrpc.TransactionServiceStub
import fima.services.transaction.TransactionService.{QueryStringFilter, SearchFilter, SearchTransactionsRequest, TransactionTagFilter}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import implicits.SubscriptionSearchQueryExt

class SubscriptionServiceImpl(subscriptionRepository: SubscriptionRepository,
                              transactionService: TransactionServiceStub,
                              transactor: Resource[IO, Transactor[IO]])
                             (private implicit val ec: ExecutionContext) extends SubscriptionService {

  override def getSubscription(request: GetSubscriptionRequest): Future[GetSubscriptionResponse] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

    (for {
      subscription <- transactor.use { xa => subscriptionRepository.findById(UUID.fromString(request.id)).transact(xa) }
      searchTransactionsResponse <- subscription.map { s =>
        IO.fromFuture(IO(transactionService.searchTransactions(s.query.asSearchRequest).map { _.transactions }))
      }.getOrElse(IO.fromFuture(IO(Future.successful(Seq.empty))))
    } yield GetSubscriptionResponse(
      subscription = subscription.map { s => Subscription(s.id.toString, s.name, Recurrence.fromValue(s.recurrence.id)) },
      transactions = searchTransactionsResponse
    )).unsafeToFuture()
  }

  override def getSubscriptions(request: GetSubscriptionsRequest): Future[GetSubscriptionsResponse] = {
    (for {
      subscriptions <- transactor.use { xa => subscriptionRepository.findAll().transact(xa) }
    } yield GetSubscriptionsResponse(
      subscriptions = subscriptions.map { s => Subscription(s.id.toString, s.name, Recurrence.MONTHLY) },
    )).unsafeToFuture()
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
