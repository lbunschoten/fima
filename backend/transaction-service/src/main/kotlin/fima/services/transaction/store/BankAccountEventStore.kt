package fima.services.transaction.store

import fima.services.transaction.write.event.Event
import org.jdbi.v3.core.Jdbi
import java.io.Closeable

class BankAccountEventStore(db: Jdbi) : EventStore(), Closeable {

    private val handle = db.open()

    override fun aggregates(): List<String> {
        return handle
            .select("SELECT DISTINCT aggregate_id FROM bank_account_events")
            .mapTo(String::class.java)
            .list()
    }

    override fun readEvents(aggregateId: String): List<Event> {
        val serializedEvents = handle
            .select("SELECT event FROM bank_account_events WHERE aggregate_id = ?", aggregateId)
            .mapTo(String::class.java)
            .list()

        return deserializeEvents(serializedEvents)
    }

    override fun writeEvents(aggregateId: String, events: List<Event>) {
        events.forEach { event ->
            handle.execute("""
              INSERT INTO bank_account_events(aggregate_id, at, version, event)
              VALUES (?, ?, ?, ?)
            """, aggregateId, event.at, event.version.toLong(), serializeEvent(event))
        }
    }

    override fun close() = handle.close()

}