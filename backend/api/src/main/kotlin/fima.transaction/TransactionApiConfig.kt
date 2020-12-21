package fima.transaction

import fima.services.transaction.TransactionServiceGrpcKt
import fima.services.transactionimport.TransactionImportServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TransactionApiConfig {

    @Value("\${TRANSACTION_SERVICE_READS_SERVICE_HOST:localhost}")
    private var transactionServiceHost: String = "localhost"

    @Value("\${TRANSACTION_SERVICE_READS_SERVICE_PORT:9997}")
    private var transactionServicePort: Int = 9997

    @Value("\${TRANSACTION_IMPORT_SERVICE_SERVICE_HOST:localhost}")
    private var transactionImportServiceHost: String = "localhost"

    @Value("\${TRANSACTION_IMPORT_SERVICE_SERVICE_PORT:9997}")
    private var transactionImportServicePort: Int = 9997

    private val logger = LoggerFactory.getLogger(TransactionApiConfig::class.java)

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
}