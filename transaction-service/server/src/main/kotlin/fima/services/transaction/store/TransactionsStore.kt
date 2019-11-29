package fima.services.transaction.store

import fima.domain.transaction.Date
import fima.domain.transaction.Transaction
import fima.services.transaction.conversion.RawTypeToTransactionTypeConverter
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

abstract class TransactionsStore {

  init {
    transaction {
      addLogger(StdOutSqlLogger)

      SchemaUtils.create(Transactions)
    }
  }

  object Transactions : IntIdTable() {
    val date = Transactions.date("date")
    val name = Transactions.varchar("name", 255)
    val fromAccount = Transactions.varchar("from_account", 255).nullable()
    val toAccount = Transactions.varchar("to_account", 255).nullable()
    val type = Transactions.varchar("type", 2)
    val amount = Transactions.long("amount")
  }

  class TransactionDao(
    id: EntityID<Int>,
    private val rawTypeToTransactionTypeConverter: RawTypeToTransactionTypeConverter = RawTypeToTransactionTypeConverter()
  ) : IntEntity(id) {
    companion object : IntEntityClass<TransactionDao>(Transactions)

    private val date by Transactions.date
    private val name by Transactions.name
    private val fromAccount by Transactions.fromAccount
    private val toAccount by Transactions.toAccount
    private val type by Transactions.type
    private val amount by Transactions.amount

    fun toProto(): Transaction {
      return Transaction
        .newBuilder()
        .setId(id.value)
        .setType(rawTypeToTransactionTypeConverter(type))
        .setDate(Date.newBuilder().setDay(date.dayOfMonth().get()).setMonth(date.monthOfYear().get()).setYear(date.year().get()).build())
        .setName(name)
        .setFromAccount(fromAccount)
        .setToAccount(toAccount)
        .setAmount(amount.toFloat())
        .build()
    }
  }

}