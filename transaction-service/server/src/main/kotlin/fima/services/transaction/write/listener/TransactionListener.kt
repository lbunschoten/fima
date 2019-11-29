package fima.services.transaction.write.listener

import fima.services.transaction.conversion.RawDateToDateConverter
import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyDepositedEvent
import fima.services.transaction.write.event.MoneyWithdrawnEvent
import fima.services.transaction.write.store.TransactionsWritesStore
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class TransactionListener(private val transactionsStore: TransactionsWritesStore,
                          private val rawDateToDateConverter: RawDateToDateConverter) : (Event) -> Unit {

  override fun invoke(event: Event) {
    when(event) {
      is MoneyDepositedEvent -> {
        val date = rawDateToDateConverter(event.date)
        transactionsStore.insertTransaction(
          date = DateTime(date.year, date.month, date.day, 0, 0, DateTimeZone.UTC),
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
          date = DateTime(date.year, date.month, date.day, 0, 0, DateTimeZone.UTC),
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