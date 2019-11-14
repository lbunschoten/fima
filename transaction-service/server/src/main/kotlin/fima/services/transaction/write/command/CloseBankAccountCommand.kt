package fima.services.transaction.write.command

import fima.services.transaction.write.event.BankAccountClosedEvent
import fima.services.transaction.write.event.Event

class CloseBankAccountCommand : Command {

  override fun events(): List<(Int) -> Event> {
    return listOf { version: Int -> BankAccountClosedEvent(version) }
  }

}