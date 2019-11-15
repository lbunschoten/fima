package fima.services.transaction.write

import fima.services.transaction.write.aggregate.BankAccount
import fima.services.transaction.write.aggregate.UniniatilizedAccount
import fima.services.transaction.write.command.WithdrawMoneyCommand
import fima.services.transaction.write.command.CloseBankAccountCommand
import fima.services.transaction.write.command.OpenBankAccountCommand
import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyDepositedEvent
import fima.services.transaction.write.listener.EventLoggingListener
import fima.services.transaction.write.store.InMemoryBankAccountEventStore
import kotlinx.serialization.ImplicitReflectionSerializer
import java.util.UUID

class TransactionCounter: (Event) -> Unit {
  private var count = 0

  override fun invoke(event: Event) {
    when(event) {
      is MoneyDepositedEvent -> {
        count++
        println(count)
      }
    }
  }

}


@ImplicitReflectionSerializer
fun main() {
  val create = OpenBankAccountCommand("ABC", 0)
  val addTransaction1 = WithdrawMoneyCommand(100)
  val addTransaction2 = WithdrawMoneyCommand(-50)
  val close = CloseBankAccountCommand()

  val eventStore = InMemoryBankAccountEventStore()
  val eventProcessor = EventProcessor()
  val commandHandler = CommandHandler(
    eventStore = eventStore,
    eventProcessor = eventProcessor,
    eventListeners = setOf(EventLoggingListener(), TransactionCounter()))
  val aggregateId: String = UUID.randomUUID().toString()

  commandHandler.processCommand(aggregateId, create)
  commandHandler.processCommand(aggregateId, addTransaction1)
  commandHandler.processCommand(aggregateId, addTransaction2)
  commandHandler.processCommand(aggregateId, close)

  val newBankAccount: BankAccount = UniniatilizedAccount
  println(eventStore.readEvents(aggregateId).fold(newBankAccount, { agg, e ->
    println(agg)
    eventProcessor.process(agg, listOf(e))
  }))
}
