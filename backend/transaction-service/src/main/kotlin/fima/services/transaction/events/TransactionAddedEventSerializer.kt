package fima.services.transaction.events

import fima.events.transaction.TransactionAddedEvent
import org.apache.kafka.common.serialization.Serializer

class TransactionAddedEventSerializer : Serializer<TransactionAddedEvent> {

    override fun configure(configs: MutableMap<String, *>, isKey: Boolean) {}

    override fun close() {}

    override fun serialize(topic: String, event: TransactionAddedEvent): ByteArray {
        return event.toByteArray()
    }

}