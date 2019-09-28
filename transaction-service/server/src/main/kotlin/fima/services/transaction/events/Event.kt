package fima.services.transaction.events

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.parse
import java.time.Instant
import java.util.UUID

@Serializable
abstract class Event : EventVersion {

  abstract val aggregateId: UUID

  abstract val version: Int

  private val at: Long = Instant.now().toEpochMilli()

  fun apply(aggregate: BankAccount?): BankAccount? {
    version = aggregate!!.version + 1
    at = Instant.now().toEpochMilli()

    return applyEvent(aggregate)
  }

  abstract fun applyEvent(aggregate: BankAccount?): BankAccount?
}

@Serializable
data class BankAccountCreated(val accountNumber: String) : Event(), EventVersion1 {

  override val aggregateId: UUID get() = UUID.randomUUID()

  override fun applyEvent(aggregate: BankAccount?): BankAccount? {
    return OnlineBankAccount(accountNumber)
  }
}


@Serializable
data class TransactionAddedEvent(val accountNumber: String) : Event(), EventVersion1 {

  override val aggregateId: Int? get() = 1

  override fun applyEvent(aggregate: OnlineBankAccount?): OnlineBankAccount? {
    return aggregate
  }
}

@Serializable
class TransactionDeletedEvent: Event(), EventVersion1 {

  override fun applyEvent(aggregate: BankAccount?): BankAccount? {
    return null
  }
}

@Serializable
data class BankAccountDeletedEvent(val a: Int) : Event(), EventVersion1 {

  override fun applyEvent(aggregate: BankAccount?): BankAccount? {
    return BankAccount(1, accountNumber)
  }
}

sealed class BankAccount {
  abstract val version: Int
}

object UniniatilizedAccount : BankAccount() {
  override val version: Int = 0
}

data class OnlineBankAccount(val accountNumber: String) : BankAccount() {
  override val version: Int = 1
}



interface EventVersion {
  val eventVersion: Int
}

interface EventVersion1 : EventVersion {
  override val eventVersion: Int get() = 1
}

interface TransactionCommand {

  fun events(): List<Event>

}

data class CreateTransactionCommand(val accountNumber: String) : TransactionCommand {

  override fun events(): List<Event> {
    return listOf<Event>(
      TransactionAddedEvent(accountNumber)
    )
  }

}

class DeleteTransactionCommand : TransactionCommand {

  override fun events(): List<Event> {
    return listOf(
      TransactionDeletedEvent()
    )
  }

}

class EventProcessor {

  fun process(events: List<Event>): BankAccount? {
    return events.fold(UniniatilizedAccount, {
      aggregate: BankAccount?, e -> e.apply(aggregate)
    })
  }

}

@ImplicitReflectionSerializer
class EventStore {

  private val eventSerializers = SerializersModule {
    polymorphic(Event::class) {
      BankAccountCreated::class with BankAccountCreated.serializer()
      TransactionAddedEvent::class with TransactionAddedEvent.serializer()
      TransactionDeletedEvent::class with TransactionDeletedEvent.serializer()
      BankAccountDeletedEvent::class with BankAccountDeletedEvent.serializer()
    }
  }

  private val storage = mutableMapOf<UUID, List<String>>() // TODO: Replace with database

  private val json = Json(context = eventSerializers)

  fun readEvents(aggregateId: UUID): List<Event> {
    val serializedEvents = storage.getOrDefault(aggregateId, emptyList())
    return serializedEvents.map { json.parse(it) }
  }

  fun writeEvents(aggregateId: UUID, events: List<Event>) {
    val serializedEvents = events.map { json.stringify(Event.serializer(), it) }
    storage[aggregateId] = serializedEvents
  }

}

@ImplicitReflectionSerializer
fun main() {
  val create = CreateTransactionCommand("ABC")
  val update = ChangeAccountNumberCommand("DEF")
  val delete = DeleteTransactionCommand()

  val a: TransactionAddedEvent = create.events()[0] as TransactionAddedEvent

  val store = EventStore()
  store.writeEvents(UUID.randomUUID(), create.events() + update.events())

//  println(json.stringify(TransactionAddedEvent.serializer(), a))

  val events = create.events() + update.events() + delete.events()
  val t = EventProcessor().process(events)
  println(t)
  println(events)
}
