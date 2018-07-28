package fima.transaction

import fima.services.transaction.TransactionServiceGrpc
import io.grpc.ManagedChannelBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TransactionApiConfig {

    @ConfigurationProperties(prefix = "transaction.service")
    class TransactionServiceProperties {
        private var host: String = "localhost"
        private var port: Int = 9997

        fun getHost(): String {
            return host
        }

        fun getPort(): Int {
            return port
        }
    }

    @Bean
    open fun getTransactionService(): TransactionServiceGrpc.TransactionServiceBlockingStub {
        val transactionServiceProperties = TransactionServiceProperties()
        val channel = ManagedChannelBuilder
                .forAddress(transactionServiceProperties.getHost(), transactionServiceProperties.getPort())
                .usePlaintext(true)
                .build()
        return TransactionServiceGrpc.newBlockingStub(channel)
    }

}