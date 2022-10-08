package fima.services

import configureRouting
import fima.services.http.plugins.configureHttp
import fima.services.http.plugins.configureSerialization
import fima.services.transaction.GrpcTransactionService
import fima.services.transaction.store.*
import fima.services.transaction.write.*
import fima.services.transaction.write.listener.EventLoggingListener
import fima.services.transaction.write.listener.TransactionListener
import fima.services.transaction.write.listener.TransactionStatisticsListener
import fima.services.transaction.write.listener.TransactionTaggingListener
import io.grpc.ServerBuilder
import io.grpc.util.TransmitStatusRuntimeExceptionInterceptor
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.slf4j.LoggerFactory

fun Application.module(
    transactionsStore: TransactionsStore,
    transactionStatisticsService: TransactionStatisticsService,
    transactionImportService: TransactionImportService,
    taggingService: TaggingService
) {
    configureHttp()
    configureRouting(transactionsStore, transactionImportService, transactionStatisticsService, taggingService)
    configureSerialization()
}

fun main() {
    val logger = LoggerFactory.getLogger("Main")
    val dbHost: String = System.getenv("FIMA_POSTGRES_DB_SERVICE_HOST") ?: "localhost"
    val dbPort: String = System.getenv("FIMA_POSTGRES_DB_SERVICE_PORT") ?: "3306"
    val dbPassword: String = System.getenv("DB_PASSWORD") ?: "root123"
    val db = Jdbi.create("jdbc:postgresql://$dbHost:$dbPort/fima?createDatabaseIfNotExist=true&currentSchema=transaction", "root", dbPassword)
        .installPlugin(KotlinPlugin())
        .installPlugin(KotlinSqlObjectPlugin())

    val bankAccountEventStore = BankAccountEventStore(db, EventSerialization())
    val transactionsStore = TransactionsStoreImpl(db, db.onDemand(TransactionsStore::class.java))
    val transactionStatisticsStore = TransactionStatisticsStoreImpl(db)
    val transactionTagsStore = TransactionTagsStore(db)
    val taggingRuleStore = TaggingRulesStoreImpl(db)
    val transactionStatisticsService = TransactionStatisticsService(bankAccountEventStore, transactionStatisticsStore)
    val taggingService = TaggingService(bankAccountEventStore, taggingRuleStore, transactionTagsStore)
    val transactionService = TransactionService(transactionsStore)

    val commandHandler = CommandHandler(
        transactionHandler = JdbiTransactionHandler(db),
        eventStore = bankAccountEventStore,
        eventProcessor = EventProcessor(),
        eventListeners = setOf(
            EventLoggingListener(),
            TransactionListener(transactionService),
            TransactionStatisticsListener(transactionStatisticsService),
            TransactionTaggingListener(taggingService)
        )
    )
    val transactionImportService = TransactionImportServiceImpl(commandHandler, bankAccountEventStore)

    val httpServer = embeddedServer(Netty, port = 9998) {
        module(transactionsStore, transactionStatisticsService, transactionImportService, taggingService)
    }.start()

    val grpcService = ServerBuilder
        .forPort(9997)
        .intercept(TransmitStatusRuntimeExceptionInterceptor.instance())
        .addService(GrpcTransactionService(transactionService))
        .build()
        .start()

    logger.info("Transaction-service started")

    Runtime.getRuntime().addShutdownHook(Thread {
        logger.info("Transaction shutting down")

        grpcService.shutdown()
        grpcService.awaitTermination()
        httpServer.stop()

        logger.info("Transaction services stopped")
    })

    grpcService.awaitTermination()
}

