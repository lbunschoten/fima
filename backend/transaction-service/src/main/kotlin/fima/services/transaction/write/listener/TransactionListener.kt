package fima.services.transaction.write.listener

import fima.services.transaction.conversion.RawDateToDateConverter
import fima.services.transaction.store.TransactionsStore
import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyDepositedEvent
import fima.services.transaction.write.event.MoneyWithdrawnEvent
import java.time.ZoneId
import java.time.ZonedDateTime

class TransactionListener(private val transactionsStore: TransactionsStore,
                          private val rawDateToDateConverter: RawDateToDateConverter) : (Event) -> Unit {

    override fun invoke(event: Event) {
        when (event) {
            is MoneyDepositedEvent -> {
                val date = rawDateToDateConverter(event.date)
                transactionsStore.insertTransaction(
                    id = event.id.toString(),
                    date = ZonedDateTime.of(date.year, date.month, date.day, 0, 0, 0, 0, ZoneId.of("UTC")),
                    name = event.name,
                    fromAccount = event.fromAccountNumber,
                    toAccount = event.toAccountNumber,
                    type = event.type,
                    amountInCents = event.amountInCents
                )
            }
            is MoneyWithdrawnEvent -> {
                val date = rawDateToDateConverter(event.date)
                transactionsStore.insertTransaction(
                    id = event.id.toString(),
                    date = ZonedDateTime.of(date.year, date.month, date.day, 0, 0, 0, 0, ZoneId.of("UTC")),
                    name = event.name,
                    fromAccount = event.fromAccountNumber,
                    toAccount = event.toAccountNumber,
                    type = event.type,
                    amountInCents = event.amountInCents
                )
            }
        }
    }

}