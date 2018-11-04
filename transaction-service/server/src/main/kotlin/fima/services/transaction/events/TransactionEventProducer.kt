package fima.services.transaction.events

import fima.events.transaction.TransactionAddedEvent
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.LongSerializer
import org.slf4j.LoggerFactory
import java.util.Properties


class TransactionEventProducer : KafkaProducer<Long, TransactionAddedEvent>(producerProperties()) {

  private val logger = LoggerFactory.getLogger(javaClass)

  companion object {
    fun producerProperties(): Properties {
      val kafkaHost: String = System.getenv("KAFKA_HOST") ?: "10.0.2.15"
      val kafkaPort: String = System.getenv("KAFKA_PORT") ?: "9092"

      val producerProps = Properties()
      producerProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$kafkaHost:$kafkaPort"
      producerProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = LongSerializer::class.java.name
      producerProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = TransactionAddedEventSerializer::class.java.name
      return producerProps
    }
  }

  fun produce(event: TransactionAddedEvent) {
    send(ProducerRecord<Long, TransactionAddedEvent>("fima-added-transactions", event)) { metadata, exception ->
      if (exception != null) {
        logger.error("Could not produce event: ${exception.message}")
      } else {
        logger.info("Produced new `TransactionAddedEvent` to topic: ${metadata.topic()}")
      }
    }
    flush()
  }

}