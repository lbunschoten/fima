package fima.services.transaction.write.command

import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyDepositedEvent

data class DepositMoneyCommand(val amountInCents: Long): Command {

  override fun events(): List<(Int) -> Event> {
    return listOf { version: Int -> MoneyDepositedEvent(version, amountInCents) }
  }
}