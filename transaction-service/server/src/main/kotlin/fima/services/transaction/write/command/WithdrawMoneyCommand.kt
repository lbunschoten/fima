package fima.services.transaction.write.command

import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyDepositedEvent
import fima.services.transaction.write.event.MoneyWithdrawnEvent

data class WithdrawMoneyCommand(val amountInCents: Long): Command {

  override fun events(): List<(Int) -> Event> {
    return listOf { version: Int -> MoneyWithdrawnEvent(version, amountInCents) }
  }
}