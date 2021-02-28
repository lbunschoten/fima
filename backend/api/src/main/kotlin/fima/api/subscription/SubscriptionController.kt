package fima.api.subscription

import fima.services.subscription.GetSubscriptionRequest
import fima.services.subscription.SubscriptionServiceGrpcKt
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class SubscriptionController @Autowired constructor(
    private val subscriptionService: SubscriptionServiceGrpcKt.SubscriptionServiceCoroutineStub,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @CrossOrigin
    @GetMapping("/subscription/{id}")
    suspend fun getSubscription(@PathVariable("id") subscriptionId: UUID): Subscription {
        logger.info("Received request to get subscription $subscriptionId")

        val request = GetSubscriptionRequest.newBuilder().setId(subscriptionId.toString()).build()

        return subscriptionService
            .getSubscription(request)
            .subscription
            .let(Subscription::fromProto)
    }

}