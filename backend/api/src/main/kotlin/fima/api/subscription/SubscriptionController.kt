package fima.api.subscription

import fima.api.transaction.Transaction
import fima.services.subscription.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import java.util.UUID

@RestController
class SubscriptionController @Autowired constructor(
    private val subscriptionService: SubscriptionServiceGrpcKt.SubscriptionServiceCoroutineStub,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @CrossOrigin
    @GetMapping("/subscription/{id}")
    suspend fun getSubscription(@PathVariable("id") subscriptionId: UUID): GetSubscriptionResponse {
        logger.info("Received request to get subscription $subscriptionId")

        val request = getSubscriptionRequest { id = subscriptionId.toString() }
        val response = subscriptionService.getSubscription(request)

        if (response.hasSubscription()) {
            return GetSubscriptionResponse(
                response.subscription.let(Subscription::fromProto),
                response.transactionsList.map(Transaction::fromProto)
            )
        } else {
            throw HttpClientErrorException(HttpStatus.NOT_FOUND)
        }
    }

    @CrossOrigin
    @GetMapping("/subscriptions")
    suspend fun getSubscriptions(): List<Subscription> {
        logger.info("Received request to get subscriptions")

        return subscriptionService
            .getSubscriptions(getSubscriptionsRequest {})
            .subscriptionsList
            .map(Subscription::fromProto)
    }

    data class GetSubscriptionResponse(
        val subscription: Subscription,
        val transactions: List<Transaction>
    )

}