package fima.services.transaction.store

import fima.services.transaction.write.event.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

interface EventStore {

    fun aggregates(): List<String>

    fun readEvents(limit: Int, offset: Int): List<Event>

    fun readEvents(aggregateId: String): List<Event>

    fun readLatestEvents(aggregateId: String): List<Event>

    fun writeEvents(aggregateId: String, events: List<Event>)

}

class EventSerialization {

    private val eventSerializers = SerializersModule {
        polymorphic(Event::class) {
            subclass(BankAccountOpenedEvent::class)
            subclass(MoneyDepositedEvent::class)
            subclass(MoneyWithdrawnEvent::class)
            subclass(BankAccountClosedEvent::class)
            subclass(BankAccountSnapshotCreatedEvent::class)
        }
    }

    private val json = Json { serializersModule = eventSerializers; classDiscriminator = "t" }

    fun serialize(event: Event): String {
        return json.encodeToString(Event.serializer(), event)
    }

    fun deserialize(serializedEvents: List<String>): List<Event> {
        return serializedEvents.map { e ->
            val event: Event = json.decodeFromString(e)
            event
        }
    }

}
