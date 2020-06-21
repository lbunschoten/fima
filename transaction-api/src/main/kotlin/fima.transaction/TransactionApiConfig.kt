package fima.transaction

import fima.services.transaction.TransactionServiceGrpcKt
import fima.services.transactionimport.TransactionImportServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableWebFlux
open class TransactionApiConfig: WebFluxConfigurer {

    @Value("\${TRANSACTION_SERVICE_READS_SERVICE_HOST:localhost}")
    private var transactionServiceHost: String = "localhost"

    @Value("\${TRANSACTION_SERVICE_READS_SERVICE_PORT:9997}")
    private var transactionServicePort: Int = 9997

    @Value("\${TRANSACTION_IMPORT_SERVICE_SERVICE_HOST:localhost}")
    private var transactionImportServiceHost: String = "localhost"

    @Value("\${TRANSACTION_IMPORT_SERVICE_SERVICE_PORT:9997}")
    private var transactionImportServicePort: Int = 9997

    @Bean
    open fun getTransactionService(): TransactionServiceGrpcKt.TransactionServiceCoroutineStub {
        val channel = ManagedChannelBuilder
                .forAddress(transactionServiceHost, transactionServicePort)
                .usePlaintext()
                .build()
        return TransactionServiceGrpcKt.TransactionServiceCoroutineStub(channel)
    }

    @Bean
    open fun getTransactionImportService(): TransactionImportServiceGrpcKt.TransactionImportServiceCoroutineStub {
        val channel = ManagedChannelBuilder
                .forAddress(transactionImportServiceHost, transactionImportServicePort)
                .usePlaintext()
                .build()
        return TransactionImportServiceGrpcKt.TransactionImportServiceCoroutineStub(channel)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST")
    }
}