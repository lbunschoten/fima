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

            SchemaUtils.create(MonthlyTransactionStatistics)
        }
    }

    fun insertTransaction(month: Int, year: Int) {
        transaction {
            val statistics = getStatistics(month, year)
            statistics?.let {
                MonthlyTransactionStatistics.update({ MonthlyTransactionStatistics.month eq month and (MonthlyTransactionStatistics.year eq year) }) {
                    it[MonthlyTransactionStatistics.month] = month
                    it[MonthlyTransactionStatistics.year] = year
                    it[numTransactions] = statistics.numTransactions + 1
                }
            } ?: {
                MonthlyTransactionStatistics.insert {
                    it[MonthlyTransactionStatistics.month] = month
                    it[MonthlyTransactionStatistics.year] = year
                    it[numTransactions] = 1
                }
            }()
        }
    }

    fun getStatistics(month: Int, year: Int): MonthlyTransactionStatisticsDao? {
        return transaction {
            MonthlyTransactionStatisticsDao.find {
                MonthlyTransactionStatistics.month eq month and (MonthlyTransactionStatistics.year eq year)
            }.firstOrNull()
        }
    }

    object MonthlyTransactionStatistics : IntIdTable() {
        val month = integer("month").index()
        val year = integer("year").index()
        val numTransactions = integer("numTransactions")
    }

    class MonthlyTransactionStatisticsDao(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<MonthlyTransactionStatisticsDao>(MonthlyTransactionStatistics)

        val month by MonthlyTransactionStatistics.month
        val year by MonthlyTransactionStatistics.year
        val numTransactions by MonthlyTransactionStatistics.numTransactions
    }

}