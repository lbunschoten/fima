package fima.services.transaction

import fima.services.transaction.conversion.RawDateToDateConverter
import fima.services.transaction.conversion.RawTransactionToTransactionConverter
import fima.services.transaction.conversion.RawTypeToTransactionTypeConverter
import fima.services.transaction.events.TransactionEventProducer
import fima.services.transaction.repository.TransactionsRepository
import io.grpc.ServerBuilder
import org.jetbrains.exposed.sql.Database


fun main(args: Array<String>) {
    // TODO: Make configurabler
    Database.connect("jdbc:mysql://localhost:3306/fima", driver = "com.mysql.cj.jdbc.Driver", user = "root", password = "root123")

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

