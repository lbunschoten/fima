package fima.services.transaction.write.store

import fima.services.transaction.write.event.Event
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.parse

class InMemoryBankAccountEventStore : EventStore() {

  private val storage = mutableMapOf<String, List<String>>()

  @ImplicitReflectionSerializer
  override fun readEvents(aggregateId: String): List<Event> {
    val serializedEvents = storage.getOrDefault(aggregateId, emptyList())
    return serializedEvents.map { e ->
      val event: Event = json.parse(e)
      event
    }
  }

  override fun writeEvents(aggregateId: String, events: List<Event>) {
    val serializedEvents = events.map { json.stringify(Event.serializer(), it) }
    storage[aggregateId] = storage[aggregateId].orEmpty() + serializedEvents
  }

}