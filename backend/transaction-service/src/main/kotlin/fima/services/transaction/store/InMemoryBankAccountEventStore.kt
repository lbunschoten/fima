package fima.services.transaction.store

import fima.services.transaction.write.event.Event

class InMemoryBankAccountEventStore : EventStore {

    private val eventSerialization = EventSerialization()
    private val storage = mutableMapOf<String, List<String>>()

    override fun aggregates(): List<String> = storage.keys.toList()

    override fun readEvents(aggregateId: String): List<Event> {
        val serializedEvents = storage.getOrDefault(aggregateId, emptyList())
        return eventSerialization.deserialize(serializedEvents)
    }

    override fun readEvents(limit: Int, offset: Int): List<Event> {
        return eventSerialization.deserialize(storage.values.flatten())
    }

    override fun readLatestEvents(aggregateId: String): List<Event> = readEvents(aggregateId)

    override fun writeEvents(aggregateId: String, events: List<Event>) {
        val serializedEvents = events.map { eventSerialization.serialize(it) }
        storage[aggregateId] = storage[aggregateId].orEmpty() + serializedEvents
    }

}