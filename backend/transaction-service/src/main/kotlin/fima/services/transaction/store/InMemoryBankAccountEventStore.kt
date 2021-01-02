package fima.services.transaction.store

import fima.services.transaction.write.event.Event
import kotlinx.serialization.decodeFromString

class InMemoryBankAccountEventStore : EventStore() {

    private val storage = mutableMapOf<String, List<String>>()

    override fun readEvents(aggregateId: String): List<Event> {
        val serializedEvents = storage.getOrDefault(aggregateId, emptyList())
        return serializedEvents.map { e ->
            val event: Event = json.decodeFromString(e)
            event
        }
    }

    override fun writeEvents(aggregateId: String, events: List<Event>) {
        val serializedEvents = events.map { json.encodeToString(Event.serializer(), it) }
        storage[aggregateId] = storage[aggregateId].orEmpty() + serializedEvents
    }

}