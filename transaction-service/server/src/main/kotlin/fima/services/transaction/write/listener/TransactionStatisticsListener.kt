package fima.services.transaction.write.listener

import fima.services.transaction.conversion.RawDateToDateConverter
import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyDepositedEvent
import fima.services.transaction.write.event.MoneyWithdrawnEvent
import fima.services.transaction.write.store.TransactionStatisticsStore

class TransactionStatisticsListener(private val transactionStatisticsStore: TransactionStatisticsStore,
                                    private val rawDateToDateConverter: RawDateToDateConverter) : (Event) -> Unit {

  override fun invoke(event: Event) {
    when(event) {
      is MoneyDepositedEvent -> {
        val date = rawDateToDateConverter(event.date)
        transactionStatisticsStore.insertTransaction(date.month, date.year, event.amountInCents)
      }
      is MoneyWithdrawnEvent -> {
        val date = rawDateToDateConverter(event.date)
        transactionStatisticsStore.insertTransaction(date.month, date.year, event.amountInCents)
      }
    }
  }

}