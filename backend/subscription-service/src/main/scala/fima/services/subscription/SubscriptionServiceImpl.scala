package fima.services.subscription

import fima.domain.subscription.SubscriptionDomain.Subscription
import fima.services.subscription.SubscriptionService.SubscriptionServiceGrpc.SubscriptionService
import fima.services.subscription.SubscriptionService.{GetSubscriptionRequest, GetSubscriptionResponse, GetSubscriptionsRequest, GetSubscriptionsResponse}

import java.util.UUID
import scala.concurrent.Future

class SubscriptionServiceImpl extends SubscriptionService {

  override def getSubscription(request: GetSubscriptionRequest): Future[GetSubscriptionResponse] = {
    Future.successful(
      GetSubscriptionResponse(Option(Subscription("test1")))
    )
  }
  
  override def getSubscriptions(request: GetSubscriptionsRequest): Future[GetSubscriptionsResponse] = {
    Future.successful(
      GetSubscriptionsResponse(subscriptions = Seq(
        Subscription(UUID.randomUUID().toString, "Spotify"),
        Subscription(UUID.randomUUID().toString, "Netflix")
      ))
    )
  }

}
