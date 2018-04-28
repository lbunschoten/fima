package fima.services.transaction.events

import fima.events.transaction.TransactionAddedEvent
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.LongSerializer
import java.util.*


class TransactionEventProducer : KafkaProducer<Long, TransactionAddedEvent>(producerProperties()) {

    companion object {
        fun producerProperties(): Properties {
            val producerProps = Properties()
            producerProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
            producerProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = LongSerializer::class.java.name
            producerProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = TransactionAddedEventSerializer::class.java.name
            return producerProps
        }
    }

    fun produce(event: TransactionAddedEvent) {
        send(ProducerRecord<Long, TransactionAddedEvent>("fima-added-transactions", event))
    }

}