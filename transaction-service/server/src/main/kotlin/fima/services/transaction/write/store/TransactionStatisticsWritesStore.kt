package fima.services.transaction.write.store

import fima.services.transaction.events.TransactionEventProducer
import fima.services.transaction.store.TransactionStatisticsStore
import org.jdbi.v3.core.Handle
import org.slf4j.LoggerFactory

class TransactionStatisticsWritesStore(
  private val handle: Handle,
  private val transactionStatisticsStore: TransactionStatisticsStore,
  private val initialBalanceInCents: Long
) {

  private val logger = LoggerFactory.getLogger(TransactionStatisticsWritesStore::class.java)

  fun insertTransaction(month: Int, year: Int, amountInCents: Long) {
    logger.info("Inserting statistic")
    try {

    } catch (e: Exception) {
      logger.error("Failed to insert statistic", e)
    }
    val statistics = transactionStatisticsStore.getStatistics(month, year).orElse(null)
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