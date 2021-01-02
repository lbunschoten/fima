package fima.services.transaction.store

import fima.services.transaction.write.event.BankAccountClosedEvent
import fima.services.transaction.write.event.BankAccountOpenedEvent
import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyDepositedEvent
import fima.services.transaction.write.event.MoneyWithdrawnEvent
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.util.UUID

abstract class EventStore {

    private val eventSerializers = SerializersModule {
        polymorphic(Event::class) {
            subclass(BankAccountOpenedEvent::class)
            subclass(MoneyDepositedEvent::class)
            subclass(MoneyWithdrawnEvent::class)
            subclass(BankAccountClosedEvent::class)
        }
    }

    val json = Json { serializersModule = eventSerializers; classDiscriminator = "t" }

    abstract fun readEvents(aggregateId: String): List<Event>

    abstract fun writeEvents(aggregateId: String, events: List<Event>)

    fun serializeEvent(event: Event): String {
        return json.encodeToString(Event.serializer(), event)
    }

    fun deserializeEvents(serializedEvents: List<String>): List<Event> {
        return serializedEvents.map { e ->
            val event: Event = json.decodeFromString(e)
            event
        }
    }

}

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }
}