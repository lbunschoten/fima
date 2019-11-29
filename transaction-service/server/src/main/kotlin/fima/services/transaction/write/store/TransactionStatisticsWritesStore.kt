package fima.services.transaction.write.store

import fima.services.transaction.store.TransactionStatisticsStore
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class TransactionStatisticsWritesStore(private val initialBalanceInCents: Long): TransactionStatisticsStore()  {

  fun insertTransaction(month: Int, year: Int, amountInCents: Long) {
    transaction {
      val statistics = getStatistics(month, year)
      statistics?.let {
        MonthlyTransactionStatisticsTable.update({ MonthlyTransactionStatisticsTable.month eq month and (MonthlyTransactionStatisticsTable.year eq year) }) {
          it[MonthlyTransactionStatisticsTable.month] = month
          it[MonthlyTransactionStatisticsTable.year] = year
          it[MonthlyTransactionStatisticsTable.numTransactions] = statistics.numTransactions + 1
          it[MonthlyTransactionStatisticsTable.sum] = statistics.sum + amountInCents
          it[MonthlyTransactionStatisticsTable.balance] = statistics.balance + amountInCents
        }
      } ?: {
        val previousMonthStatistics = getPreviousMonthStatistics(month, year)

        MonthlyTransactionStatisticsTable.insert {
          it[MonthlyTransactionStatisticsTable.month] = month
          it[MonthlyTransactionStatisticsTable.year] = year
          it[MonthlyTransactionStatisticsTable.numTransactions] = 1
          it[MonthlyTransactionStatisticsTable.sum] = amountInCents
          it[MonthlyTransactionStatisticsTable.balance] = previousMonthStatistics?.balance ?: initialBalanceInCents + amountInCents
        }
      }()
    }
  }

}