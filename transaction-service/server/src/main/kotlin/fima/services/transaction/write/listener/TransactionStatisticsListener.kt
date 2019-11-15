package fima.services.transaction.write.listener

import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyDepositedEvent
import fima.services.transaction.write.event.MoneyWithdrawnEvent
import fima.services.transaction.write.store.TransactionStatisticsStore
import java.time.Instant
import java.time.ZoneId

class TransactionStatisticsListener(private val transactionStatisticsStore: TransactionStatisticsStore) : (Event) -> Unit {

  override fun invoke(event: Event) {
    val date = Instant.ofEpochMilli(event.at).atZone(ZoneId.systemDefault()).toLocalDate()
    when(event) {
      is MoneyDepositedEvent -> transactionStatisticsStore.insertTransaction(date.month.value, date.year, event.amountInCents.toDouble() / 100)
      is MoneyWithdrawnEvent -> transactionStatisticsStore.insertTransaction(date.month.value, date.year, event.amountInCents.toDouble() / 100)
    }
  }

}