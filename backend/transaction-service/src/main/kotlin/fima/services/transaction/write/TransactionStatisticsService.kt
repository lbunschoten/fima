package fima.services.transaction.write

import fima.services.transaction.store.BankAccountEventStore
import fima.services.transaction.store.MonthlyTransactionStatistics
import fima.services.transaction.store.TransactionStatisticsStoreImpl
import fima.services.transaction.write.event.TransactionEvent
import java.time.LocalDate

class TransactionStatisticsService(
    private val bankAccountEventStore: BankAccountEventStore,
    private val transactionStatisticsStore: TransactionStatisticsStoreImpl
) {

    fun getMonthlyStatistics(): List<MonthlyTransactionStatistics> {
        return transactionStatisticsStore.getMonthlyStatistics(1, 2021, 12, 2021)
    }

    fun resetStatistics() {
        transactionStatisticsStore.deleteAllStatistics()
        resetStatistics(limit = 100, offset = 0)
    }

    fun addTransaction(date: LocalDate, amountInCentsDiff: Long) {
        transactionStatisticsStore.insertTransaction(date.monthValue, date.year, amountInCentsDiff)
    }

    private fun resetStatistics(limit: Int, offset: Int) {
        val events = bankAccountEventStore.readEvents(limit, offset)

        events
            .filterIsInstance<TransactionEvent>()
            .forEach {
                addTransaction(it.date, it.amountInCentsDiff)
            }

        if (events.isNotEmpty()) {
            resetStatistics(limit, offset + limit)
        }
    }

}