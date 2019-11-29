package fima.services.transaction.read.store

import fima.services.transaction.store.TransactionStatisticsStore
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction

class TransactionStatisticsReadsStore: TransactionStatisticsStore() {

  fun getMonthlyStatistics(startMonth: Int, startYear: Int, endMonth: Int, endYear: Int): List<MonthlyTransactionStatistics> {
    return transaction {
      MonthlyTransactionStatisticsDao.find {
        (
          (MonthlyTransactionStatisticsTable.month greaterEq startMonth and (MonthlyTransactionStatisticsTable.year greaterEq startYear)) or
            (MonthlyTransactionStatisticsTable.year greater startYear)
          ) and (
          (MonthlyTransactionStatisticsTable.month lessEq endMonth and (MonthlyTransactionStatisticsTable.year lessEq endYear)) or
            (MonthlyTransactionStatisticsTable.year less endYear)
          )
      }.sortedWith(Comparator { o1, o2 ->
        if (o1.year > o2.year || (o1.year == o2.year && o1.month > o2.month)) 1
        else if (o1.year < o2.year || (o1.year == o2.year && o1.month < o2.month)) -1
        else 0
      }).toList().map { it.simple() }
    }
  }

}