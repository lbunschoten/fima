package fima.services.transaction.write.listener

import fima.services.transaction.write.event.Event

class EventLoggingListener : (Event) -> Unit {

  override fun invoke(event: Event) {
    println(event.toString())
  }

}