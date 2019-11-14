package fima.services.transactionimport

import fima.services.transaction.write.TransactionWritesServiceGrpc
import io.grpc.ManagedChannelBuilder
import io.grpc.ServerBuilder


fun main(args: Array<String>) {
    val transactionServiceHost = System.getenv("TRANSACTION_SERVICE_SERVICE_HOST")
    val transactionServiceWritesPort = System.getenv("TRANSACTION_SERVICE_SERVICE_PORT").toInt()

    val transactionService: TransactionWritesServiceGrpc.TransactionWritesServiceBlockingStub = {
        val channel = ManagedChannelBuilder.forAddress(transactionServiceHost, transactionServiceWritesPort).usePlaintext().build()
        TransactionWritesServiceGrpc.newBlockingStub(channel)
    }()

    val server = ServerBuilder
            .forPort(9997)
            .addService(TransactionImportServiceImpl(
                    transactionService = transactionService
            ))
            .build()

    server.start()
    println("Transaction import service started")

    Runtime.getRuntime().addShutdownHook(Thread { println("Ups, JVM shutdown") })
    server.awaitTermination()

    println("Transaction import service stopped")
}

