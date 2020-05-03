package fima.services.transaction

import fima.services.transaction.conversion.RawDateToDateConverter
import fima.services.transaction.read.TransactionReadsServiceImpl
import fima.services.transaction.read.store.TransactionReads
import fima.services.transaction.read.store.TransactionStatisticsStore
import fima.services.transaction.write.CommandHandler
import fima.services.transaction.write.EventProcessor
import fima.services.transaction.write.TransactionWritesServiceImpl
import fima.services.transaction.write.listener.EventLoggingListener
import fima.services.transaction.write.listener.TransactionListener
import fima.services.transaction.write.listener.TransactionStatisticsListener
import fima.services.transaction.write.store.BankAccountEventStore
import fima.services.transaction.write.store.TransactionStatisticsWritesStore
import io.grpc.ServerBuilder
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import fima.services.transaction.write.store.TransactionsWritesStore as TransactionWritesStore


fun main() {
  val dbHost: String = System.getenv("FIMA_MYSQL_DB_SERVICE_HOST") ?: "localhost"
  val dbPort: String = System.getenv("FIMA_MYSQL_DB_SERVICE_PORT") ?: "3306"
  val dbPassword: String = System.getenv("DB_PASSWORD") ?: "root123"
  val db = Jdbi.create("jdbc:mysql://$dbHost:$dbPort/transaction?createDatabaseIfNotExist=true", "root", dbPassword)
    .installPlugin(KotlinPlugin())
    .installPlugin(KotlinSqlObjectPlugin())

  val readSideServer = ServerBuilder
    .forPort(9997)
    .addService(TransactionReadsServiceImpl(
      transactionsStore = db.onDemand(TransactionReads::class.java),
      transactionStatisticsStore = db.onDemand(TransactionStatisticsStore::class.java)
    ))
    .build()

  val bankAccountEventStoreHandle = db.open()
  val transactionStatisticsWritesStoreHandle = db.open()

  val writeSideServer = ServerBuilder
    .forPort(9998)
    .addService(TransactionWritesServiceImpl(
      CommandHandler(
        BankAccountEventStore(bankAccountEventStoreHandle),
        EventProcessor(),
        setOf(
          EventLoggingListener(),
          TransactionListener(db.onDemand(TransactionWritesStore::class.java), RawDateToDateConverter()),
          TransactionStatisticsListener(
            TransactionStatisticsWritesStore(
              transactionStatisticsWritesStoreHandle,
              db.onDemand(fima.services.transaction.store.TransactionStatisticsStore::class.java),
              0L
            ), RawDateToDateConverter()))
      )
    ))
    .build()

  readSideServer.start()
  writeSideServer.start()
  println("Transaction services started")

  Runtime.getRuntime().addShutdownHook(Thread {
    println("JVM is shutting down")
    bankAccountEventStoreHandle.close()
    transactionStatisticsWritesStoreHandle.close()
  })
  readSideServer.awaitTermination()
  writeSideServer.awaitTermination()

  println("Transaction services stopped")
}

