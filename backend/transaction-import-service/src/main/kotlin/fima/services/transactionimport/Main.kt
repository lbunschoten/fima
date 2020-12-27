package fima.services.transactionimport

import fima.services.transaction.write.TransactionWritesServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import io.grpc.ServerBuilder
import org.slf4j.LoggerFactory


fun main() {
    val transactionServiceHost = System.getenv("TRANSACTION_SERVICE_WRITES_SERVICE_HOST")
    val transactionServiceWritesPort = System.getenv("TRANSACTION_SERVICE_WRITES_SERVICE_PORT").toInt()

    val channel = ManagedChannelBuilder.forAddress(transactionServiceHost, transactionServiceWritesPort).usePlaintext().build()
    val transactionService: TransactionWritesServiceGrpcKt.TransactionWritesServiceCoroutineStub = TransactionWritesServiceGrpcKt.TransactionWritesServiceCoroutineStub(channel)

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

