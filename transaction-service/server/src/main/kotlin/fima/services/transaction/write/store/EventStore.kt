package fima.services.transaction.write.store

import fima.services.transaction.write.event.*
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PrimitiveDescriptor
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.Serializer
import kotlinx.serialization.decode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.parse
import java.util.UUID

abstract class EventStore {

  private val eventSerializers = SerializersModule {
    polymorphic<Event> {
      BankAccountOpenedEvent::class with BankAccountOpenedEvent.serializer()
      MoneyDepositedEvent::class with MoneyDepositedEvent.serializer()
      MoneyWithdrawnEvent::class with MoneyWithdrawnEvent.serializer()
      BankAccountClosedEvent::class with BankAccountClosedEvent.serializer()
    }
  }

  val json = Json(context = eventSerializers, configuration = JsonConfiguration.Stable.copy(classDiscriminator = "t"))

  abstract fun readEvents(aggregateId: String): List<Event>

  abstract fun writeEvents(aggregateId: String, events: List<Event>)

  fun serializeEvent(event: Event): String {
    return json.stringify(Event.serializer(), event)
  }

  @ImplicitReflectionSerializer
  fun deserializeEvents(serializedEvents: List<String>): List<Event> {
    return serializedEvents.map { e ->
      val event: Event = json.parse(e)
      event
    }
  }

}

@Serializer(forClass = UUID::class)
object UUIDSerializer : KSerializer<UUID> {
  override val descriptor: SerialDescriptor
    get() = PrimitiveDescriptor("UUID", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: UUID) {
    encoder.encodeString(value.toString())
  }

  @ImplicitReflectionSerializer
  override fun deserialize(decoder: Decoder): UUID {
    return UUID.fromString(decoder.decode())
  }
}