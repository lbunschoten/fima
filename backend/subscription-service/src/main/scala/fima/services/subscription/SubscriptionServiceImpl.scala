package fima.services.subscription

import cats.effect.{ContextShift, IO, Resource}
import doobie.implicits._
import doobie.{ExecutionContexts, Transactor}
import fima.domain.subscription.SubscriptionDomain.{Recurrence, Subscription}
import fima.services.subscription.SubscriptionService.SubscriptionServiceGrpc.SubscriptionService
import fima.services.subscription.SubscriptionService.{GetSubscriptionRequest, GetSubscriptionResponse, GetSubscriptionsRequest, GetSubscriptionsResponse}
import fima.services.transaction.TransactionService.TransactionServiceGrpc.TransactionServiceStub
import fima.services.transaction.TransactionService.{SearchFilter, SearchTransactionsRequest, TransactionTagFilter}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class SubscriptionServiceImpl(subscriptionRepository: SubscriptionRepository,
                              transactionService: TransactionServiceStub,
                              transactor: Resource[IO, Transactor[IO]])
                             (private implicit val ec: ExecutionContext) extends SubscriptionService {

  override def getSubscription(request: GetSubscriptionRequest): Future[GetSubscriptionResponse] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

    val searchTransactionsRequest = SearchTransactionsRequest()
      .withQuery("SPOTIFY")
      .withFilters(Seq(
        SearchFilter(Seq(
          TransactionTagFilter("recurring", "monthly")
        ))
      ))

    (for {
      s <- transactor.use { xa => subscriptionRepository.findById(UUID.fromString(request.id)).transact(xa).map { _.getOrElse(throw new Exception(""))} }
      t <- IO.fromFuture(IO(transactionService.searchTransactions(searchTransactionsRequest)))
    } yield GetSubscriptionResponse(
      subscription = Option(Subscription(s.id.toString, s.name, Recurrence.fromValue(s.recurrence.id))),
      transactions = t.transactions
    )).unsafeToFuture()
  }

  override def getSubscriptions(request: GetSubscriptionsRequest): Future[GetSubscriptionsResponse] = {
    (for {
      s <- transactor.use { xa => subscriptionRepository.findAll().transact(xa) }
    } yield GetSubscriptionsResponse(
      subscriptions = s.map { ss => Subscription(ss.id.toString, ss.name, Recurrence.MONTHLY)},
    )).unsafeToFuture()
  }

}
