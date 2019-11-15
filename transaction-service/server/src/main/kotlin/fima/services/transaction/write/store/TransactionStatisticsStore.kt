package fima.services.transaction.write.store

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

class TransactionStatisticsStore(private val initialBalance: Double) {

  init {
    transaction {
      logger.addLogger(StdOutSqlLogger)

      SchemaUtils.create(MonthlyTransactionStatisticsTable)
    }
  }

  fun insertTransaction(month: Int, year: Int, amount: Double) {
    transaction {
      val statistics = getStatistics(month, year)
      statistics?.let {
        MonthlyTransactionStatisticsTable.update({ MonthlyTransactionStatisticsTable.month eq month and (MonthlyTransactionStatisticsTable.year eq year) }) {
          it[MonthlyTransactionStatisticsTable.month] = month
          it[MonthlyTransactionStatisticsTable.year] = year
          it[MonthlyTransactionStatisticsTable.numTransactions] = statistics.numTransactions + 1
          it[MonthlyTransactionStatisticsTable.sum] = statistics.sum.add(BigDecimal(amount))
          it[MonthlyTransactionStatisticsTable.balance] = statistics.balance.add(BigDecimal(amount))
        }
      } ?: {
        val previousMonthStatistics = getPreviousMonthStatistics(month, year)

        MonthlyTransactionStatisticsTable.insert {
          it[MonthlyTransactionStatisticsTable.month] = month
          it[MonthlyTransactionStatisticsTable.year] = year
          it[MonthlyTransactionStatisticsTable.numTransactions] = 1
          it[MonthlyTransactionStatisticsTable.sum] = BigDecimal(amount)
          it[MonthlyTransactionStatisticsTable.balance] = (previousMonthStatistics?.balance ?: BigDecimal(initialBalance)).add(BigDecimal(amount))
        }
      }()
    }
  }

  private fun getStatistics(month: Int, year: Int): MonthlyTransactionStatistics? {
    return transaction {
      MonthlyTransactionStatisticsDao.find {
        MonthlyTransactionStatisticsTable.month eq month and (MonthlyTransactionStatisticsTable.year eq year)
      }.firstOrNull()?.simple()
    }
  }

  private fun getPreviousMonthStatistics(month: Int, year: Int): MonthlyTransactionStatistics? {
    return {
      if (month == 1) getStatistics(12, year - 1)
      else getStatistics(month - 1, year)
    }()
  }

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

  object MonthlyTransactionStatisticsTable : IntIdTable() {
    val month = integer("month").index()
    val year = integer("year").index()
    val numTransactions = integer("numTransactions")
    val sum = decimal("sum", 9, 2)
    val balance = decimal("balance", 9, 2)
  }

  class MonthlyTransactionStatisticsDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MonthlyTransactionStatisticsDao>(MonthlyTransactionStatisticsTable)

    val month by MonthlyTransactionStatisticsTable.month
    val year by MonthlyTransactionStatisticsTable.year
    val numTransactions by MonthlyTransactionStatisticsTable.numTransactions
    val sum by MonthlyTransactionStatisticsTable.sum
    val balance by MonthlyTransactionStatisticsTable.balance

    fun simple(): MonthlyTransactionStatistics {
      return MonthlyTransactionStatistics(month, year, numTransactions, sum, balance)
    }
  }

  data class MonthlyTransactionStatistics(
    val month: Int,
    val year: Int,
    val numTransactions: Int,
    val sum: BigDecimal,
    val balance: BigDecimal
  )

}