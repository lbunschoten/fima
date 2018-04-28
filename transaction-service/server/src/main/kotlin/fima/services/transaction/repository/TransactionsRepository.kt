package fima.services.transaction.repository

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.insert
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.math.BigDecimal
import org.jetbrains.exposed.sql.transactions.transaction as dbtransaction

class TransactionsRepository {

    init {
        dbtransaction {
            logger.addLogger(StdOutSqlLogger)

            SchemaUtils.create(Transactions)
        }
    }

    fun insertTransaction(transaction: fima.domain.transaction.Transaction) {
        dbtransaction {
            Transactions.insert {
                it[date] = DateTime(transaction.date.year, transaction.date.month, transaction.date.day, 0, 0, DateTimeZone.UTC)
                it[name] = transaction.name
                it[fromAccount] = transaction.fromAccount
                it[toAccount] = transaction.toAccount
                it[type] = transaction.typeValue
                it[amount] = BigDecimal(transaction.amount.toDouble())
            }
        }
    }

//    fun getStatistics(month: Int, year: Int): MonthlyTransactionStatisticsDao? {
//        return dbtransaction {
//            MonthlyTransactionStatisticsDao.find {
//                MonthlyTransactionStatistics.month eq month and (MonthlyTransactionStatistics.year eq year)
//            }.firstOrNull()
//        }
//    }

    object Transactions : IntIdTable() {
        val date = date("date")
        val name = varchar("name", 255)
        val fromAccount = varchar("from_account", 255).nullable()
        val toAccount = varchar("to_account", 255).nullable()
        val type = integer("type")
        val amount = decimal("amount", 9, 2)
    }

    class TransactionDao(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<TransactionDao>(Transactions)

        val date by Transactions.date
        val name by Transactions.name
        val fromAccount by Transactions.fromAccount
        val toAccount by Transactions.toAccount
        val type by Transactions.type
        val amount by Transactions.amount
    }

}