package fima.services.transaction.write.command

import fima.services.transaction.write.event.Event

interface Command {

  fun events(): List<(Int) -> Event>

}