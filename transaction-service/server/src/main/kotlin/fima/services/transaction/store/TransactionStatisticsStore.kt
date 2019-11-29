package fima.services.transaction.store

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

abstract class TransactionStatisticsStore {

  init {
    transaction {
      addLogger(StdOutSqlLogger)

      SchemaUtils.create(MonthlyTransactionStatisticsTable)
    }
  }

  protected fun getStatistics(month: Int, year: Int): MonthlyTransactionStatistics? {
    return transaction {
      MonthlyTransactionStatisticsDao.find {
        MonthlyTransactionStatisticsTable.month eq month and (MonthlyTransactionStatisticsTable.year eq year)
      }.firstOrNull()?.simple()
    }
  }

  protected fun getPreviousMonthStatistics(month: Int, year: Int): MonthlyTransactionStatistics? {
    return {
      if (month == 1) getStatistics(12, year - 1)
      else getStatistics(month - 1, year)
    }()
  }

  object MonthlyTransactionStatisticsTable : IntIdTable() {
    val month = integer("month").index()
    val year = integer("year").index()
    val numTransactions = integer("numTransactions")
    val sum = long("sum")
    val balance = long("balance")
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
    val sum: Long,
    val balance: Long
  )

}