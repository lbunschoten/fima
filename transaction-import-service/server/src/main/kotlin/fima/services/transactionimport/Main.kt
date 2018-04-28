package fima.services.transactionimport

import fima.services.transaction.TransactionServiceGrpc
import io.grpc.ManagedChannelBuilder


fun main(args: Array<String>) {
    val transactionService: TransactionServiceGrpc.TransactionServiceBlockingStub = {
        val channel = ManagedChannelBuilder.forAddress("localhost", 9997).usePlaintext(true).build()
        TransactionServiceGrpc.newBlockingStub(channel)
    }()

    TransactionImportServiceImpl(transactionService)

    Runtime.getRuntime().addShutdownHook(Thread { println("Ups, JVM shutdown") })

    println("Transaction statistics service stopped")
}

