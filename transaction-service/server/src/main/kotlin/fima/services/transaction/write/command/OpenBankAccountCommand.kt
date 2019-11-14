package fima.services.transaction.write.command

import fima.services.transaction.write.event.BankAccountOpenedEvent
import fima.services.transaction.write.event.Event

data class OpenBankAccountCommand(val accountNumber: String) : Command {

  override fun events(): List<(Int) -> Event> {
    return listOf { version: Int -> BankAccountOpenedEvent(version, accountNumber) }
  }

}