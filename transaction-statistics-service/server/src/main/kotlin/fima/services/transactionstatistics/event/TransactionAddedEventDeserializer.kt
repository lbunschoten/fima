package fima.services.transactionstatistics.event

import fima.events.transaction.TransactionAddedEvent
import org.apache.kafka.common.serialization.Deserializer

class TransactionAddedEventDeserializer : Deserializer<TransactionAddedEvent> {

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {

    }

    override fun close() {

    }

    override fun deserialize(topic: String, eventBytes: ByteArray): TransactionAddedEvent {
        return TransactionAddedEvent.parseFrom(eventBytes)
    }

}