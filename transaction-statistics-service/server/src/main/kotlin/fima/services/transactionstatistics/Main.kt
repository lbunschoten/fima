package fima.services.transactionstatistics

import fima.events.transaction.TransactionAddedEvent
import fima.services.transactionstatistics.event.EventListener
import fima.services.transactionstatistics.event.ProcessTransactionAddedEvent
import fima.services.transactionstatistics.event.TransactionAddedEventDeserializer
import fima.services.transactionstatistics.repository.StatisticsRepository
import io.grpc.ServerBuilder
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.LongDeserializer
import org.jetbrains.exposed.sql.Database
import java.util.*


fun main(args: Array<String>) {
    // TODO: Make configurabler
    Database.connect("jdbc:mysql://localhost:3306/fima", driver = "com.mysql.cj.jdbc.Driver", user = "root", password = "root123")

    val statisticsRepository = StatisticsRepository()
    val transactionAddedEventProcessor = ProcessTransactionAddedEvent(statisticsRepository)
    EventListener<TransactionAddedEvent>({
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ConsumerConfig.GROUP_ID_CONFIG] = "fima-transaction-statistics-transaction-added-consumer"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = LongDeserializer::class.java.name
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = TransactionAddedEventDeserializer::class.java.name
        props[ConsumerConfig.AUTO_OFFSET_RESET_DOC] = "latest"
        props
    }(), "fima-added-transactions").listen(transactionAddedEventProcessor)

    val server = ServerBuilder.forPort(15001).addService(TransactionStatisticsServiceImpl(statisticsRepository)).build()
    server.start()
    println("Transaction statistics service started")

    Runtime.getRuntime().addShutdownHook(Thread { println("Ups, JVM shutdown") })
    server.awaitTermination()

    println("Transaction statistics service stopped")
}

