package fima.api

import fima.services.subscription.InvestmentServiceGrpcKt
import fima.services.subscription.SubscriptionServiceGrpcKt
import fima.services.transaction.TransactionServiceGrpcKt
import fima.services.transactionimport.TransactionImportServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TransactionApiConfig {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${TRANSACTION_SERVICE_SERVICE_HOST:localhost}")
    private var transactionServiceHost: String = "localhost"

    @Value("\${TRANSACTION_SERVICE_SERVICE_PORT:9997}")
    private var transactionServicePort: Int = 9997

    @Value("\${TRANSACTION_IMPORT_SERVICE_SERVICE_HOST:localhost}")
    private var transactionImportServiceHost: String = "localhost"

    @Value("\${TRANSACTION_IMPORT_SERVICE_SERVICE_PORT:9997}")
    private var transactionImportServicePort: Int = 9997

    @Value("\${SUBSCRIPTION_SERVICE_SERVICE_HOST:localhost}")
    private var subscriptionServiceHost: String = "localhost"

    @Value("\${SUBSCRIPTION_SERVICE_SERVICE_PORT:9997}")
    private var subscriptionServicePort: Int = 9997

    @Value("\${INVESTMENT_SERVICE_SERVICE_HOST:localhost}")
    private var investmentServiceHost: String = "localhost"

    @Value("\${INVESTMENT_SERVICE_SERVICE_PORT:9997}")
    private var investmentServicePort: Int = 9997

    @Bean
    open fun getTransactionService(): TransactionServiceGrpcKt.TransactionServiceCoroutineStub {
        logger.info("Connecting to transaction-service at ${transactionServiceHost}:${transactionServicePort}")
        val channel = ManagedChannelBuilder
            .forAddress(transactionServiceHost, transactionServicePort)
            .usePlaintext()
            .build()
        return TransactionServiceGrpcKt.TransactionServiceCoroutineStub(channel)
    }

    @Bean
    open fun getTransactionImportService(): TransactionImportServiceGrpcKt.TransactionImportServiceCoroutineStub {
        logger.info("Connecting to transaction-import-service at ${transactionImportServiceHost}:${transactionImportServicePort}")
        val channel = ManagedChannelBuilder
            .forAddress(transactionImportServiceHost, transactionImportServicePort)
            .usePlaintext()
            .build()
        return TransactionImportServiceGrpcKt.TransactionImportServiceCoroutineStub(channel)
    }

    @Bean
    open fun getSubscriptionService(): SubscriptionServiceGrpcKt.SubscriptionServiceCoroutineStub {
        logger.info("Connecting to subscription-service at ${subscriptionServiceHost}:${subscriptionServicePort}")
        val channel = ManagedChannelBuilder
            .forAddress(subscriptionServiceHost, subscriptionServicePort)
            .usePlaintext()
            .build()
        return SubscriptionServiceGrpcKt.SubscriptionServiceCoroutineStub(channel)
    }

    @Bean
    open fun getInvestmentService(): InvestmentServiceGrpcKt.InvestmentServiceCoroutineStub {
        logger.info("Connecting to investment-service at ${investmentServiceHost}:${investmentServicePort}")
        val channel = ManagedChannelBuilder
            .forAddress(investmentServiceHost, investmentServicePort)
            .usePlaintext()
            .build()
        return InvestmentServiceGrpcKt.InvestmentServiceCoroutineStub(channel)
    }
}