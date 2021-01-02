package fima.services.transaction.write.command

import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyDepositedEvent
import java.util.UUID

data class DepositMoneyCommand(val amountInCents: Long,
                               val date: Int,
                               val name: String,
                               val details: String,
                               val fromAccountNumber: String,
                               val type: String) : Command {

    override fun events(aggregateId: String): List<(Int) -> Event> {
        return listOf { version: Int -> MoneyDepositedEvent(version, UUID.randomUUID(), amountInCents, date, name, details, fromAccountNumber, aggregateId, type) }
    }
}