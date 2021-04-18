package fima.services.subscription

import cats.effect.{IO, Resource}
import doobie.Transactor
import doobie.implicits._
import fima.domain.subscription.SubscriptionDomain.{Recurrence, Subscription}
import fima.services.subscription.SubscriptionService.SubscriptionServiceGrpc.SubscriptionService
import fima.services.subscription.SubscriptionService.{GetSubscriptionRequest, GetSubscriptionResponse, GetSubscriptionsRequest, GetSubscriptionsResponse}
import fima.services.transaction.TransactionService.GetRecentTransactionsRequest
import fima.services.transaction.TransactionService.TransactionServiceGrpc.TransactionServiceBlockingStub

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class SubscriptionServiceImpl(subscriptionRepository: SubscriptionRepository,
                              transactionService: TransactionServiceBlockingStub,
                              transactor: Resource[IO, Transactor[IO]])
                             (private implicit val ec: ExecutionContext) extends SubscriptionService {

  override def getSubscription(request: GetSubscriptionRequest): Future[GetSubscriptionResponse] = {

    val t = transactionService.getRecentTransactions(GetRecentTransactionsRequest().withLimit(10).withOffset(0))
    println(t)

    (for {
      s <- transactor.use { xa => subscriptionRepository.findById(UUID.fromString(request.id)).transact(xa) }
      t <- IO.pure(Option(t.transactions))
    } yield GetSubscriptionResponse(
      subscription = s.map { ss => Subscription(ss.id.toString, ss.name, Recurrence.fromValue(ss.recurrence.id))},
      transactions = t.getOrElse(Seq.empty)
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
