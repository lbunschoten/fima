package fima.services.transaction.write.command

import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyWithdrawnEvent
import java.time.LocalDate
import java.util.UUID

data class WithdrawMoneyCommand(val amountInCents: Long,
                                val date: LocalDate,
                                val name: String,
                                val details: String,
                                val toAccountNumber: String,
                                val type: String) : Command {

    override fun events(aggregateId: String): List<(Int, Int) -> Event> {
        return listOf { version: Int, snapshotVersion: Int -> MoneyWithdrawnEvent(version, snapshotVersion, UUID.randomUUID(), amountInCents, date, name, details, aggregateId, toAccountNumber, type) }
    }
}
