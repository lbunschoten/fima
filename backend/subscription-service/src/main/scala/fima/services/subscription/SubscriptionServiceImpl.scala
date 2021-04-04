package fima.services.subscription

import fima.domain.subscription.SubscriptionDomain.Subscription
import fima.services.subscription.SubscriptionService.SubscriptionServiceGrpc.SubscriptionService
import fima.services.subscription.SubscriptionService.{GetSubscriptionRequest, GetSubscriptionResponse, GetSubscriptionsRequest, GetSubscriptionsResponse}

import java.util.UUID
import scala.concurrent.Future

class SubscriptionServiceImpl extends SubscriptionService {

  private val subscriptions = Seq(
    Subscription("15300971-6e0c-4990-a2fc-2d718a113820", "Spotifiy"),
    Subscription("125300971-6e0c-4990-a2fc-2d718a113820", "Netflix")
  )

  override def getSubscription(request: GetSubscriptionRequest): Future[GetSubscriptionResponse] = {
    Future.successful(GetSubscriptionResponse(subscriptions.find(request.id == _.id)))
  }

  override def getSubscriptions(request: GetSubscriptionsRequest): Future[GetSubscriptionsResponse] = {
    Future.successful(GetSubscriptionsResponse(subscriptions))
  }

}
