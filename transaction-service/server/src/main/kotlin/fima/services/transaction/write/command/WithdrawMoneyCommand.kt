package fima.services.transaction.write.command

import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyWithdrawnEvent

data class WithdrawMoneyCommand(val amountInCents: Long,
                                val date: Int,
                                val name: String,
                                val details: String,
                                val toAccountNumber: String,
                                val type: String) : Command {

  override fun events(aggregateId: String): List<(Int) -> Event> {
    return listOf { version: Int -> MoneyWithdrawnEvent(version, amountInCents, date, name, details, aggregateId, toAccountNumber, type) }
  }
}
