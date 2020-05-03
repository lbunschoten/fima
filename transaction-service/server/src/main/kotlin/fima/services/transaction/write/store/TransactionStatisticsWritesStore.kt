package fima.services.transaction.write.store

import fima.services.transaction.store.TransactionStatisticsStore
import org.jdbi.v3.core.Handle

class TransactionStatisticsWritesStore(
  private val handle: Handle,
  private val transactionStatisticsStore: TransactionStatisticsStore,
  private val initialBalanceInCents: Long
) {

  fun insertTransaction(month: Int, year: Int, amountInCents: Long) {
    val statistics = transactionStatisticsStore.getStatistics(month, year)
    statistics?.let {
      updateStatistic(
        numTransactions = statistics.numTransactions,
        sum = statistics.sum + amountInCents,
        balance = statistics.balance + amountInCents,
        month = month,
        year = year
      )
    } ?: run {
      val previousMonthStatistics = transactionStatisticsStore.getPreviousMonthStatistics(month, year)
      insertStatistic(
        sum = amountInCents,
        balance = previousMonthStatistics?.balance ?: initialBalanceInCents + amountInCents,
        month = month,
        year = year
      )
    }
  }

  private fun insertStatistic(sum: Long, balance: Long, month: Int, year: Int) {
    handle.execute("""
      INSERT INTO MonthlyTransactionStatistics (month, year, numTransactions, sum, balance)
      VALUES (?, ?, 1, ?, ?)
    """, month, year, sum, balance)
  }

  private fun updateStatistic(numTransactions: Int, sum: Long, balance: Long, month: Int, year: Int) {
    handle.execute("""
        UPDATE MonthlyTransactionStatistics
        SET 
          numTransactions = ?,
          sum = ?,
          balance = ?
        WHERE month = ? AND year = ?
        """,
      numTransactions, sum, balance, month, year
    )
  }
}