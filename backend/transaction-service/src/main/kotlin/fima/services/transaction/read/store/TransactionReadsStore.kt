package fima.services.transaction.read.store

import fima.services.transaction.store.Transaction
import fima.services.transaction.store.TransactionMapper
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery

interface TransactionReads {

  @SqlQuery("SELECT * FROM Transactions WHERE id = :id")
  @RegisterRowMapper(TransactionMapper::class)
  fun getById(id: String): Transaction

  @SqlQuery("SELECT * FROM Transactions ORDER BY `date` DESC LIMIT :limit OFFSET :offset")
  @RegisterRowMapper(TransactionMapper::class)
  fun getRecent(offset: Int, limit: Int): List<Transaction>
}