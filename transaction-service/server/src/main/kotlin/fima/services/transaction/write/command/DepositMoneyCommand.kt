package fima.services.transaction.write.command

import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyDepositedEvent

data class DepositMoneyCommand(val amountInCents: Long,
                               val date: Int,
                               val name: String,
                               val details: String,
                               val accountNumber: String,
                               val type: String): Command {

  override fun events(): List<(Int) -> Event> {
    return listOf { version: Int -> MoneyDepositedEvent(version, amountInCents, date, name, details, accountNumber, type) }
  }
}