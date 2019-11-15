package fima.services.transaction.write.store

import fima.services.transaction.write.event.*
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.parse

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