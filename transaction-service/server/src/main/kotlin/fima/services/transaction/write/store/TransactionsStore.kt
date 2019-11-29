package fima.services.transaction.write.store

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.joda.time.DateTime
import org.jetbrains.exposed.sql.transactions.transaction as dbtransaction

class TransactionsStore {

  init {
    dbtransaction {
      addLogger(StdOutSqlLogger)

      SchemaUtils.create(Transactions)
    }
  }

  fun insertTransaction(date: DateTime, name: String, fromAccount: String, toAccount: String, type: String, amountInCents: Long) {
    dbtransaction {
      Transactions.insert {
        it[Transactions.date] = date
        it[Transactions.name] = name
        it[Transactions.fromAccount] = fromAccount
        it[Transactions.toAccount] = toAccount
        it[Transactions.type] = type
        it[Transactions.amount] = amountInCents
      }
    }
  }

  object Transactions : IntIdTable() {
    val date = date("date")
    val name = varchar("name", 255)
    val fromAccount = varchar("from_account", 255).nullable()
    val toAccount = varchar("to_account", 255).nullable()
    val type = varchar("type", 5)
    val amount = long("amount")
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