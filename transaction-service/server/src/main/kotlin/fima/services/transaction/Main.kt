package fima.services.transaction

import fima.services.transaction.conversion.RawDateToDateConverter
import fima.services.transaction.read.TransactionReadsServiceImpl
import fima.services.transaction.write.CommandHandler
import fima.services.transaction.write.EventProcessor
import fima.services.transaction.write.TransactionWritesServiceImpl
import fima.services.transaction.write.listener.EventLoggingListener
import fima.services.transaction.write.listener.TransactionListener
import fima.services.transaction.write.listener.TransactionStatisticsListener
import fima.services.transaction.write.store.BankAccountEventStore
import fima.services.transaction.write.store.TransactionStatisticsStore
import io.grpc.ServerBuilder
import org.jetbrains.exposed.sql.Database
import fima.services.transaction.read.store.TransactionsStore as TransactionReadsStore
import fima.services.transaction.write.store.TransactionsStore as TransactionWritesStore


fun main() {
  val dbHost: String = System.getenv("FIMA_MYSQL_DB_SERVICE_HOST") ?: "localhost"
  val dbPort: String = System.getenv("FIMA_MYSQL_DB_SERVICE_PORT") ?: "3306"
  val dbPassword: String = System.getenv("DB_PASSWORD") ?: "root123"
  Database.connect("jdbc:mysql://$dbHost:$dbPort/transaction?createDatabaseIfNotExist=true", driver = "com.mysql.cj.jdbc.Driver", user = "root", password = dbPassword)

  val readSideServer = ServerBuilder
    .forPort(9997)
    .addService(TransactionReadsServiceImpl(
      transactionsStore = TransactionReadsStore()
    ))
    .build()

  val writeSideServer = ServerBuilder
    .forPort(9998)
    .addService(TransactionWritesServiceImpl(
      CommandHandler(
        BankAccountEventStore(),
        EventProcessor(),
        setOf(
          EventLoggingListener(),
          TransactionListener(TransactionWritesStore(), RawDateToDateConverter()),
          TransactionStatisticsListener(TransactionStatisticsStore(0L), RawDateToDateConverter()))
      )
    ))
    .build()

  readSideServer.start()
  writeSideServer.start()
  println("Transaction services started")

  Runtime.getRuntime().addShutdownHook(Thread { println("Ups, JVM shutdown") })
  readSideServer.awaitTermination()
  writeSideServer.awaitTermination()

  println("Transaction services stopped")
}

