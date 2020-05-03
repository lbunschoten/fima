package fima.services.transaction.write.store

import fima.services.transaction.write.event.Event
import kotlinx.serialization.ImplicitReflectionSerializer
import org.jdbi.v3.core.Handle

class BankAccountEventStore(private val handle: Handle) : EventStore() {

  @ImplicitReflectionSerializer
  override fun readEvents(aggregateId: String): List<Event> {
    val serializedEvents = handle
      .select("SELECT event FROM BankAccountEvents WHERE aggregate_id = ?", aggregateId)
      .mapTo(String::class.java)
      .list()

    return deserializeEvents(serializedEvents)
  }

  override fun writeEvents(aggregateId: String, events: List<Event>) {
    events.forEach { event ->
      handle.execute("""
          INSERT INTO (aggregate_id, at, version, event)
          VALUES (?, ?, ?, ?)
        """, aggregateId, event.at, event.version.toLong(), serializeEvent(event))
    }
  }

}