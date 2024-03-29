package fima.services.transaction.write

import fima.services.transaction.write.aggregate.BankAccount
import fima.services.transaction.write.aggregate.UniniatilizedAccount
import fima.services.transaction.write.event.Event

class EventProcessor {

    fun process(aggregateId: String, events: List<Event>): BankAccount {
        return process(UniniatilizedAccount(aggregateId), events)
    }

    private fun process(aggregate: BankAccount, events: List<Event>): BankAccount {
        return events.fold(aggregate) { agg, e ->
            e.apply(agg)
        }
    }

}