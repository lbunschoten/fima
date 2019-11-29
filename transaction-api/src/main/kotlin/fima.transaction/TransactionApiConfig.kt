package fima.transaction

import fima.services.transaction.TransactionServiceGrpc
import fima.services.transactionimport.TransactionImportServiceGrpc
import io.grpc.ManagedChannelBuilder
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

    @Bean
    open fun getTransactionService(): TransactionServiceGrpc.TransactionServiceBlockingStub {
        val channel = ManagedChannelBuilder
                .forAddress(transactionServiceHost, transactionServicePort)
                .usePlaintext()
                .build()
        return TransactionServiceGrpc.newBlockingStub(channel)
    }

    @Bean
    open fun getTransactionImportService(): TransactionImportServiceGrpc.TransactionImportServiceBlockingStub {
        val channel = ManagedChannelBuilder
                .forAddress(transactionImportServiceHost, transactionImportServicePort)
                .usePlaintext()
                .build()
        return TransactionImportServiceGrpc.newBlockingStub(channel)
    }

}