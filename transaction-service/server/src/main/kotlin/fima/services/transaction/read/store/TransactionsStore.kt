package fima.services.transaction.read.store

import fima.domain.transaction.Date
import fima.domain.transaction.Transaction
import fima.domain.transaction.TransactionType
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction as dbtransaction

class TransactionsStore {

  init {
    dbtransaction {
      addLogger(StdOutSqlLogger)

      SchemaUtils.create(Transactions)
    }
  }

  fun getById(id: Int): TransactionDao? {
    return dbtransaction {
      TransactionDao.findById(id)
    }
  }

  fun getRecent(offset: Int, limit: Int): List<TransactionDao> {
    return dbtransaction {
      TransactionDao.wrapRows(
        Transactions
          .selectAll()
          .limit(limit, offset)
          .orderBy(Transactions.date to SortOrder.DESC, Transactions.id to SortOrder.DESC)
      ).toList()
    }
  }

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

    fun toProto(): Transaction {
      return Transaction
        .newBuilder()
        .setId(id.value)
        .setType(TransactionType.forNumber(type))
        .setDate(Date.newBuilder().setDay(date.dayOfMonth().get()).setMonth(date.monthOfYear().get()).setYear(date.year().get()).build())
        .setName(name)
        .setFromAccount(fromAccount)
        .setToAccount(toAccount)
        .setAmount(amount.toFloat())
        .build()
    }
  }

}