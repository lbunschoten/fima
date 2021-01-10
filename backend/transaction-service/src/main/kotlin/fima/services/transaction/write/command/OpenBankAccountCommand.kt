package fima.services.transaction.write.command

import fima.services.transaction.write.event.BankAccountOpenedEvent
import fima.services.transaction.write.event.Event

data class OpenBankAccountCommand(val accountNumber: String, val initialBalanceInCents: Long) : Command {

    override fun events(aggregateId: String): List<(Int) -> Event> {
        return listOf { version: Int -> BankAccountOpenedEvent(version, accountNumber, initialBalanceInCents) }
    }

}