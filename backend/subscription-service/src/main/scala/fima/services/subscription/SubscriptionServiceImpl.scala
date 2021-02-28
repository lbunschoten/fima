package fima.services.subscription

import fima.domain.subscription.SubscriptionDomain.Subscription
import fima.services.subscription.SubscriptionService.SubscriptionServiceGrpc.SubscriptionService
import fima.services.subscription.SubscriptionService.{GetSubscriptionRequest, GetSubscriptionResponse}

import scala.concurrent.Future

class SubscriptionServiceImpl extends SubscriptionService {

  override def getSubscription(request: GetSubscriptionRequest): Future[GetSubscriptionResponse] = {
    Future.successful(
      GetSubscriptionResponse(Option(Subscription("test1")))
    )
  }

}
