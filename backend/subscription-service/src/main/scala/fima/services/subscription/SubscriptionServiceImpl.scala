package fima.services.subscription

import fima.domain.subscription.SubscriptionDomain.Subscription
import fima.services.subscription.SubscriptionService.SubscriptionServiceGrpc.SubscriptionService
import fima.services.subscription.SubscriptionService.{GetSubscriptionRequest, GetSubscriptionResponse, GetSubscriptionsRequest, GetSubscriptionsResponse}
import fima.services.transaction.TransactionService.GetRecentTransactionsRequest
import fima.services.transaction.TransactionService.TransactionServiceGrpc.TransactionServiceBlockingStub

import scala.concurrent.Future

class SubscriptionServiceImpl(transactionService: TransactionServiceBlockingStub) extends SubscriptionService {

  private val subscriptions = Seq(
    Subscription("15300971-6e0c-4990-a2fc-2d718a113820", "Spotifiy"),
    Subscription("25300971-6e0c-4990-a2fc-2d718a113820", "Netflix")
  )

  override def getSubscription(request: GetSubscriptionRequest): Future[GetSubscriptionResponse] = {
    val transactions = transactionService.getRecentTransactions(GetRecentTransactionsRequest().withLimit(10).withOffset(0)).transactions
    Future.successful(
      GetSubscriptionResponse(
        subscription = subscriptions.find(request.id == _.id),
        transactions = transactions
      )
    )
  }

  override def getSubscriptions(request: GetSubscriptionsRequest): Future[GetSubscriptionsResponse] = {
    Future.successful(GetSubscriptionsResponse(subscriptions))
  }

}
