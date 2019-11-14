package fima.services.transaction.write.store

import fima.services.transaction.write.event.Event
import kotlinx.serialization.ImplicitReflectionSerializer
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction as dbtransaction

class BankAccountEventStore : EventStore() {

  init {
    dbtransaction {
      logger.addLogger(StdOutSqlLogger)

      SchemaUtils.create(BankAccountEvents)
    }
  }

  @ImplicitReflectionSerializer
  override fun readEvents(aggregateId: String): List<Event> {
    val serializedEvents = dbtransaction {
      BankAccountEventsDao.find { BankAccountEvents.aggregateId eq aggregateId }.toList().map { it.event }
    }

    return deserializeEvents(serializedEvents)
  }

  override fun writeEvents(aggregateId: String, events: List<Event>) {
    dbtransaction {
      events.forEach { event ->
        BankAccountEvents.insert {
          it[BankAccountEvents.aggregateId] = aggregateId
          it[BankAccountEvents.at] = event.at
          it[BankAccountEvents.version] = event.version.toLong()
          it[BankAccountEvents.event] = serializeEvent(event)
        }
      }
    }
  }

  object BankAccountEvents : IntIdTable() {
    val aggregateId = varchar("aggregate_id", 255).index()
    val at = long("at")
    val version = long("version")
    val event = text("event")
  }

  class BankAccountEventsDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BankAccountEventsDao>(BankAccountEvents)

    val aggregateId by BankAccountEvents.aggregateId
    val at by BankAccountEvents.at
    val version by BankAccountEvents.version
    val event by BankAccountEvents.event

  }

}