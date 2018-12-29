package fima.services.transactionstatistics.repository

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class StatisticsRepository {

    init {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            SchemaUtils.create(MonthlyTransactionStatisticsTable)
        }
    }

    fun insertTransaction(month: Int, year: Int) {
        transaction {
            val statistics = getStatistics(month, year)
            statistics?.let {
                MonthlyTransactionStatisticsTable.update({ MonthlyTransactionStatisticsTable.month eq month and (MonthlyTransactionStatisticsTable.year eq year) }) {
                    it[MonthlyTransactionStatisticsTable.month] = month
                    it[MonthlyTransactionStatisticsTable.year] = year
                    it[numTransactions] = statistics.numTransactions + 1
                }
            } ?: {
                MonthlyTransactionStatisticsTable.insert {
                    it[MonthlyTransactionStatisticsTable.month] = month
                    it[MonthlyTransactionStatisticsTable.year] = year
                    it[numTransactions] = 1
                }
            }()
        }
    }

    fun getStatistics(month: Int, year: Int): MonthlyTransactionStatistics? {
        return transaction {
            MonthlyTransactionStatisticsDao.find {
                MonthlyTransactionStatisticsTable.month eq month and (MonthlyTransactionStatisticsTable.year eq year)
            }.firstOrNull()?.simple()
        }
    }

    fun getMonthlyStatistics(startMonth: Int, startYear: Int, endMonth: Int, endYear: Int): List<MonthlyTransactionStatistics> {
        return transaction { MonthlyTransactionStatisticsDao.find {
            (
                (MonthlyTransactionStatisticsTable.month greaterEq startMonth and (MonthlyTransactionStatisticsTable.year greaterEq startYear)) or
                (MonthlyTransactionStatisticsTable.year greater startYear)
            ) and (
              (MonthlyTransactionStatisticsTable.month lessEq endMonth and (MonthlyTransactionStatisticsTable.year lessEq  endYear)) or
              (MonthlyTransactionStatisticsTable.year less endYear)
            )
          }.toList().map { it.simple() }
        }
    }

    object MonthlyTransactionStatisticsTable : IntIdTable() {
        val month = integer("month").index()
        val year = integer("year").index()
        val numTransactions = integer("numTransactions")
    }

    class MonthlyTransactionStatisticsDao(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<MonthlyTransactionStatisticsDao>(MonthlyTransactionStatisticsTable)

        val month by MonthlyTransactionStatisticsTable.month
        val year by MonthlyTransactionStatisticsTable.year
        val numTransactions by MonthlyTransactionStatisticsTable.numTransactions

        fun simple(): MonthlyTransactionStatistics {
            return MonthlyTransactionStatistics(month, year, numTransactions)
        }
    }

    data class MonthlyTransactionStatistics(
      val month: Int,
      val year: Int,
      val numTransactions: Int
    )

}