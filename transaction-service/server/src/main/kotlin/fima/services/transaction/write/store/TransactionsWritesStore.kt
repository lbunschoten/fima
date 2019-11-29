package fima.services.transaction.write.store

import fima.services.transaction.store.TransactionsStore
import org.jetbrains.exposed.sql.insert
import org.joda.time.DateTime
import org.jetbrains.exposed.sql.transactions.transaction as dbtransaction

class TransactionsWritesStore : TransactionsStore() {

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

}