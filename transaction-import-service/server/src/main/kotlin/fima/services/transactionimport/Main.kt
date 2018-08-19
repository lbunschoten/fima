package fima.services.transactionimport

import fima.services.transaction.TransactionServiceGrpc
import io.grpc.ManagedChannelBuilder
import io.grpc.ServerBuilder


fun main(args: Array<String>) {
    val transactionServiceHost: String = System.getenv("TRANSACTION_SERVICE_HOST") ?: "transaction-service"
    val transactionServicePort: Int = System.getenv("TRANSACTION_SERVICE_PORT")?.toInt() ?: 9997

    val transactionService: TransactionServiceGrpc.TransactionServiceBlockingStub = {
        val channel = ManagedChannelBuilder.forAddress(transactionServiceHost, transactionServicePort).usePlaintext().build()
        TransactionServiceGrpc.newBlockingStub(channel)
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

