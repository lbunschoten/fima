package fima.services.transaction.write.store

import fima.services.transaction.write.event.BankAccountClosedEvent
import fima.services.transaction.write.event.BankAccountOpenedEvent
import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyDepositedEvent
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.parse

abstract class EventStore {

  private val eventSerializers = SerializersModule {
    polymorphic(Event::class) {
      BankAccountOpenedEvent::class with BankAccountOpenedEvent.serializer()
      MoneyDepositedEvent::class with MoneyDepositedEvent.serializer()
      BankAccountClosedEvent::class with BankAccountClosedEvent.serializer()
    }
  }

  val json = Json(context = eventSerializers)

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