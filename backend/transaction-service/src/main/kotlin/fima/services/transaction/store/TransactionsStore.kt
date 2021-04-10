package fima.services.transaction.store

import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.time.ZonedDateTime

interface TransactionsStore {

    @SqlQuery("""
        SELECT 
            t.*, 
            (
                SELECT GROUP_CONCAT(DISTINCT CONCAT_WS(':', `key`, `value`) ORDER BY `key` SEPARATOR ',') 
                FROM transaction_tags tt WHERE transaction_id = t.id
            ) as tags
        FROM transactions t
    """)
    @RegisterRowMapper(TransactionRowMapper::class)
    fun getTransactions(): Set<Transaction>

    @SqlQuery("""
        SELECT 
            t.*, 
            (
                SELECT GROUP_CONCAT(DISTINCT CONCAT_WS(':', `key`, `value`) ORDER BY `key` SEPARATOR ',') 
                FROM transaction_tags tt WHERE transaction_id = t.id
            ) as tags
        FROM transactions t
        WHERE t.id = :id
    """)
    @RegisterRowMapper(TransactionRowMapper::class)
    fun getById(id: String): Transaction

    @SqlQuery("""
        SELECT 
            t.*, 
            (
                SELECT GROUP_CONCAT(DISTINCT CONCAT_WS(':', `key`, `value`) ORDER BY `key` SEPARATOR ',') 
                FROM transaction_tags tt WHERE transaction_id = t.id
            ) as tags
        FROM transactions t
        ORDER BY t.`date` DESC LIMIT :limit OFFSET :offset
    """)
    @RegisterRowMapper(TransactionRowMapper::class)
    fun getRecent(offset: Int, limit: Int): List<Transaction>

    @SqlUpdate("""
        INSERT INTO transactions(id, date, name, from_account, to_account, type, amount)
        VALUES (:id, :date, :name, :fromAccount, :toAccount, :type, :amountInCents)
    """)
    fun insertTransaction(id: String, date: ZonedDateTime, name: String, fromAccount: String, toAccount: String, type: String, amountInCents: Long)

}