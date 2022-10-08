package fima.services.transaction.store

import fima.services.transaction.write.event.Event
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked

class BankAccountEventStore(private val db: Jdbi, private val eventSerialization: EventSerialization) : EventStore {

    override fun aggregates(): List<String> {
        return db.withHandleUnchecked { handle ->
            handle
                .select("SELECT DISTINCT aggregate_id FROM bank_account_events")
                .mapTo(String::class.java)
                .list()
        }
    }

    override fun readEvents(aggregateId: String): List<Event> {
        val serializedEvents = db.withHandleUnchecked { handle ->
            handle
                .select("SELECT event FROM bank_account_events WHERE aggregate_id = ?", aggregateId)
                .mapTo(String::class.java)
                .list()
        }
        return eventSerialization.deserialize(serializedEvents)
    }

    override fun readEvents(limit: Int, offset: Int): List<Event> {
        val serializedEvents = db.withHandleUnchecked { handle ->
            handle
                .select("""
                    SELECT event
                    FROM transaction.bank_account_events
                    WHERE (event::json->'date')::text IS NOT NULL
                    ORDER BY (event::json->'date')::text,  aggregate_id, (event::json->'version')::text
                    LIMIT ? OFFSET ?
                """.trimIndent(), limit, offset)
                .mapTo(String::class.java)
                .list()
        }
        return eventSerialization.deserialize(serializedEvents)
    }

    override fun readLatestEvents(aggregateId: String): List<Event> {
        val serializedEvents = db.withHandleUnchecked { handle ->
            handle
                .select("""
                    SELECT event 
                    FROM bank_account_events 
                    WHERE aggregate_id = ?
                    AND (SELECT MAX(snapshot_version) FROM bank_account_events WHERE aggregate_id = ?) = snapshot_version
                    ORDER BY version ASC
                """.trimIndent(), aggregateId, aggregateId)
                .mapTo(String::class.java)
                .list()
        }
        return eventSerialization.deserialize(serializedEvents)
    }

    override fun writeEvents(aggregateId: String, events: List<Event>) {
        db.withHandleUnchecked { handle ->
            events.forEach { event ->
                handle.execute("""
                  INSERT INTO bank_account_events(aggregate_id, at, version, snapshot_version, event)
                  VALUES (?, ?, ?, ?, ?)
                """, aggregateId, event.at, event.version.toLong(), event.snapshotVersion.toLong(), eventSerialization.serialize(event))
            }
        }
    }

}