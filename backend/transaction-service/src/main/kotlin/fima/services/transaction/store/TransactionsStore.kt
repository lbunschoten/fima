package fima.services.transaction.store

import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.time.ZonedDateTime

interface TransactionsStore {

    @SqlQuery("SELECT * FROM Transactions WHERE id = :id")
    @RegisterRowMapper(TransactionMapper::class)
    fun getById(id: String): Transaction

    @SqlQuery("SELECT * FROM Transactions ORDER BY `date` DESC LIMIT :limit OFFSET :offset")
    @RegisterRowMapper(TransactionMapper::class)
    fun getRecent(offset: Int, limit: Int): List<Transaction>

    @SqlUpdate("""
    INSERT INTO Transactions(id, date, name, from_account, to_account, type, amount)
    VALUES (:id, :date, :name, :fromAccount, :toAccount, :type, :amountInCents)
  """)
    fun insertTransaction(id: String, date: ZonedDateTime, name: String, fromAccount: String, toAccount: String, type: String, amountInCents: Long)

}