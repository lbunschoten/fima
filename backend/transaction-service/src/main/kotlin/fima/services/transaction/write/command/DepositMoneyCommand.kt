package fima.services.transaction.write.command

import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyDepositedEvent
import java.time.LocalDate
import java.util.*

data class DepositMoneyCommand(val amountInCents: Long,
                               val date: LocalDate,
                               val name: String,
                               val details: String,
                               val fromAccountNumber: String,
                               val type: String) : Command {

    override fun events(aggregateId: String): List<(Int, Int) -> Event> {
        return listOf { version: Int, snapshotVersion: Int -> MoneyDepositedEvent(version, snapshotVersion, UUID.randomUUID(), amountInCents, date, name, details, fromAccountNumber, aggregateId, type) }
    }
}