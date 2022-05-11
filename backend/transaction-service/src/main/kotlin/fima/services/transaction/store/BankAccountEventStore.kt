package fima.services.transaction.store

import fima.services.transaction.write.event.Event
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.slf4j.LoggerFactory

class BankAccountEventStore(private val db: Jdbi, private val eventSerialization: EventSerialization) : EventStore {

    private val logger = LoggerFactory.getLogger(javaClass)

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
            logger.info("Read events: ${handle.isInTransaction }}")
            handle
                .select("SELECT event FROM bank_account_events WHERE aggregate_id = ?", aggregateId)
                .mapTo(String::class.java)
                .list()
        }
        return eventSerialization.deserialize(serializedEvents)
    }

    override fun writeEvents(aggregateId: String, events: List<Event>) {
        db.withHandleUnchecked { handle ->
            logger.info("Write events: ${handle.isInTransaction }")
            events.forEach { event ->
                handle.execute("""
                  INSERT INTO bank_account_events(aggregate_id, at, version, event)
                  VALUES (?, ?, ?, ?)
                """, aggregateId, event.at, event.version.toLong(), eventSerialization.serialize(event))
            }
        }
    }

}