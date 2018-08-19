package fima.transaction

import fima.services.transaction.TransactionServiceGrpc
import fima.services.transactionimport.TransactionImportServiceGrpc
import fima.services.transactionstatistics.TransactionStatisticsServiceGrpc
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TransactionApiConfig {

    @Value("\${transaction.service.host:localhost}")
    private var transactionServiceHost: String = "localhost"

    @Value("\${transaction.service.port:9997}")
    private var transactionServicePort: Int = 9997

    @Value("\${transaction.statistics.service.host:localhost}")
    private var transactionStatisticsServiceHost: String = "localhost"

    @Value("\${transaction.statistics.service.port:9997}")
    private var transactionStatisticsServicePort: Int = 9997

    @Value("\${transaction.import.service.host:localhost}")
    private var transactionImportServiceHost: String = "localhost"

    @Value("\${transaction.import.service.port:9997}")
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
    open fun getTransactionStatisticsService(): TransactionStatisticsServiceGrpc.TransactionStatisticsServiceBlockingStub {
        val channel = ManagedChannelBuilder
                .forAddress(transactionStatisticsServiceHost, transactionStatisticsServicePort)
                .usePlaintext()
                .build()
        return TransactionStatisticsServiceGrpc.newBlockingStub(channel)
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