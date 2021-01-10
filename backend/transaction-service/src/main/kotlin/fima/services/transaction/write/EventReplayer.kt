package fima.services.transaction.write

import fima.services.transaction.store.InMemoryBankAccountEventStore
import fima.services.transaction.write.aggregate.BankAccount
import fima.services.transaction.write.aggregate.UniniatilizedAccount
import fima.services.transaction.write.command.CloseBankAccountCommand
import fima.services.transaction.write.command.OpenBankAccountCommand
import fima.services.transaction.write.command.WithdrawMoneyCommand
import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyDepositedEvent
import fima.services.transaction.write.listener.EventLoggingListener
import java.util.UUID

class TransactionCounter : (Event) -> Unit {
    private var count = 0

    override fun invoke(event: Event) {
        when (event) {
            is MoneyDepositedEvent -> {
                count++
                println(count)
            }
        }
    }

}


fun main() {
    val create = OpenBankAccountCommand("ABC", 0)
    val addTransaction1 = WithdrawMoneyCommand(10000, 20170101, "name", "details", "ACCOUNT_NUMBER", "type")
    val addTransaction2 = WithdrawMoneyCommand(-5000, 20170101, "name", "details", "ACCOUNT_NUMBER", "type")
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
        eventProcessor.process(agg, listOf(e)).also {
            EventLoggingListener().invoke(e)
        }
    }))
}
