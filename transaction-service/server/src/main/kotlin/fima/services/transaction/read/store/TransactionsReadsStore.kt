package fima.services.transaction.read.store

import fima.services.transaction.store.TransactionsStore
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction as dbtransaction

class TransactionsReadsStore : TransactionsStore() {

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

}