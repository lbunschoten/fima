package fima.services.transaction

import fima.services.transaction.conversion.RawDateToDateConverter
import fima.services.transaction.store.BankAccountEventStore
import fima.services.transaction.store.TaggingRulesStoreImpl
import fima.services.transaction.store.TransactionStatisticsStoreImpl
import fima.services.transaction.store.TransactionTagsStore
import fima.services.transaction.store.TransactionsStore
import fima.services.transaction.write.CommandHandler
import fima.services.transaction.write.EventProcessor
import fima.services.transaction.write.TaggingService
import fima.services.transaction.write.listener.EventLoggingListener
import fima.services.transaction.write.listener.TransactionListener
import fima.services.transaction.write.listener.TransactionStatisticsListener
import fima.services.transaction.write.listener.TransactionTaggingListener
import io.grpc.ServerBuilder
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.slf4j.LoggerFactory


fun main() {
    val dbHost: String = System.getenv("FIMA_POSTGRES_DB_SERVICE_HOST") ?: "localhost"
    val dbPort: String = System.getenv("FIMA_POSTGRES_DB_SERVICE_PORT") ?: "3306"
    val dbPassword: String = System.getenv("DB_PASSWORD") ?: "root123"
    val db = Jdbi.create("jdbc:postgresql://$dbHost:$dbPort/fima?createDatabaseIfNotExist=true&currentSchema=transaction", "root", dbPassword)
        .installPlugin(KotlinPlugin())
        .installPlugin(KotlinSqlObjectPlugin())

    val bankAccountEventStore = BankAccountEventStore(db)
    val transactionsStore = db.onDemand(TransactionsStore::class.java)
    val transactionStatisticsStore = TransactionStatisticsStoreImpl(db, initialBalanceInCents = 0L)
    val transactionTagsStore = TransactionTagsStore(db)
    val taggingRuleStore = TaggingRulesStoreImpl(db)

    val taggingService = TaggingService(
        bankAccountEventStore,
        taggingRuleStore,
        transactionTagsStore
    )

    val transactionServiceServer = ServerBuilder
        .forPort(9997)
        .addService(TransactionServiceImpl(
            transactionsStore = transactionsStore,
            transactionStatisticsStore = transactionStatisticsStore,
            taggingRuleStore = taggingRuleStore,
            commandHandler = CommandHandler(
                eventStore = bankAccountEventStore,
                eventProcessor = EventProcessor(),
                eventListeners = setOf(
                    EventLoggingListener(),
                    TransactionListener(transactionsStore, RawDateToDateConverter()),
                    TransactionStatisticsListener(transactionStatisticsStore, RawDateToDateConverter()),
                    TransactionTaggingListener(taggingService)
                )
            ),
            taggingService = taggingService
        ))
        .build()

    transactionServiceServer.start()

    val logger = LoggerFactory.getLogger("Main")
    logger.info("Transaction-service started")

    Runtime.getRuntime().addShutdownHook(Thread {
        logger.info("JVM is shutting down")
        bankAccountEventStore.close()
        transactionTagsStore.close()
        transactionStatisticsStore.close()
        taggingRuleStore.close()
    })
    transactionServiceServer.awaitTermination()

    logger.info("Transaction services stopped")
}

