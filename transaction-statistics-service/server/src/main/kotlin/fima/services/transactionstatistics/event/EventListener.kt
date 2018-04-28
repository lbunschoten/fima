package fima.services.transactionstatistics.event

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.*
import java.util.Collections.singletonList
import kotlin.concurrent.thread


class EventListener<T>(props: Properties, topic: String) : KafkaConsumer<Long, T>(props) {

    init {
        subscribe(singletonList(topic))
    }

    fun listen(f: (ConsumerRecord<Long, T>) -> Unit) {
        thread {
            println("Started consuming transaction events")

            try {
                while (true) {
                    val consumerRecords = poll(1000)
                    consumerRecords.forEach(f)
                    commitAsync()
                }
            } finally {
                close()
                println("Stopped consuming transaction events")
            }
        }
    }

//    private fun buildConsumer(): KafkaConsumer<Long, T> {
//        val props = Properties()
//        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
//        props[ConsumerConfig.GROUP_ID_CONFIG] = "fima-transaction-statistics-consumer"
//        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = LongDeserializer::class.java.name
//        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = TransactionEventDeserializer::class.java.name
//        props[ConsumerConfig.AUTO_OFFSET_RESET_DOC] = "earliest"
//
//        val consumer = KafkaConsumer<Long, T>(props)
//        consumer.subscribe(Collections.singletonList("fima-transactions"))
//        return consumer
//    }

}