package fima.services.transactionimport

import fima.services.transaction.TransactionServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import io.grpc.ServerBuilder
import org.slf4j.LoggerFactory


fun main() {
    val transactionServiceHost = System.getenv("TRANSACTION_SERVICE_SERVICE_HOST")
    val transactionServicePort = System.getenv("TRANSACTION_SERVICE_SERVICE_PORT").toInt()

    val channel = ManagedChannelBuilder.forAddress(transactionServiceHost, transactionServicePort).usePlaintext().build()
    val transactionService: TransactionServiceGrpcKt.TransactionServiceCoroutineStub = TransactionServiceGrpcKt.TransactionServiceCoroutineStub(channel)

    val server = ServerBuilder
        .forPort(9997)
        .addService(TransactionImportServiceImpl(transactionService))
        .build()

    server.start()

    val logger = LoggerFactory.getLogger("Main")
    logger.info("Transaction import service started")

    Runtime.getRuntime().addShutdownHook(Thread { println("Ups, JVM shutdown") })
    server.awaitTermination()

    logger.info("Transaction import service stopped")
}

