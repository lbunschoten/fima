package fima.services.transactionimport

import fima.services.transaction.TransactionServiceGrpc
import io.grpc.ManagedChannelBuilder
import io.grpc.ServerBuilder


fun main(args: Array<String>) {
    val transactionService: TransactionServiceGrpc.TransactionServiceBlockingStub = {
        val channel = ManagedChannelBuilder.forAddress("localhost", 9997).usePlaintext().build()
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

