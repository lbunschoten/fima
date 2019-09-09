package fima.services.transaction

import fima.services.transaction.conversion.RawDateToDateConverter
import fima.services.transaction.conversion.RawTransactionToTransactionConverter
import fima.services.transaction.conversion.RawTypeToTransactionTypeConverter
import fima.services.transaction.events.TransactionEventProducer
import fima.services.transaction.repository.TransactionsRepository
import io.grpc.ServerBuilder
import org.jetbrains.exposed.sql.Database


fun main(args: Array<String>) {
    val dbHost: String = System.getenv("FIMA_MYSQL_DB_SERVICE_HOST") ?: "localhost"
    val dbPort: String = System.getenv("FIMA_MYSQL_DB_SERVICE_PORT") ?: "3306"
    val dbPassword: String = System.getenv("DB_PASSWORD") ?: "root123"
    Database.connect("jdbc:mysql://$dbHost:$dbPort/transaction?createDatabaseIfNotExist=true", driver = "com.mysql.cj.jdbc.Driver", user = "root", password = dbPassword)

    val transactionEventProducer = TransactionEventProducer()
    val server = ServerBuilder
            .forPort(9997)
            .addService(TransactionServiceImpl(
                    transactionsRepository = TransactionsRepository(),
                    transactionEventProducer = transactionEventProducer,
                    toTransactionConverter = RawTransactionToTransactionConverter(
                            toDateConverter = RawDateToDateConverter(),
                            toTransactionTypeConverter = RawTypeToTransactionTypeConverter()
                    )))
            .build()

    server.start()
    println("Transaction service started")

    Runtime.getRuntime().addShutdownHook(Thread { println("Ups, JVM shutdown") })
    server.awaitTermination()

    println("Transaction service stopped")
}

