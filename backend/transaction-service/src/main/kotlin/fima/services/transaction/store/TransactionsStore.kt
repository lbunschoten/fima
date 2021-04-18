package fima.services.transaction.store

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.time.ZonedDateTime

interface TransactionsStore {

    @SqlQuery("""
        SELECT 
            t.*,
            (
               SELECT string_agg(DISTINCT CONCAT_WS(':', key, value), ',' ORDER BY CONCAT_WS(':', key, value))
               FROM transaction_tags tt
               WHERE transaction_id = t.id
            ) as tags
        FROM transactions t
    """)
    @RegisterRowMapper(TransactionRowMapper::class)
    fun getTransactions(): Set<Transaction>

    @SqlQuery("""
        SELECT 
            t.*,
            (
               SELECT string_agg(DISTINCT CONCAT_WS(':', key, value), ',' ORDER BY CONCAT_WS(':', key, value))
               FROM transaction_tags tt
               WHERE transaction_id = t.id
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
               SELECT string_agg(DISTINCT CONCAT_WS(':', key, value), ',' ORDER BY CONCAT_WS(':', key, value))
               FROM transaction_tags tt
               WHERE transaction_id = t.id
            ) as tags
        FROM transactions t
        ORDER BY t.date DESC LIMIT :limit OFFSET :offset
    """)
    @RegisterRowMapper(TransactionRowMapper::class)
    fun getRecent(offset: Int, limit: Int): List<Transaction>

    @SqlUpdate("""
        INSERT INTO transactions(id, date, name, from_account, to_account, type, amount)
        VALUES (:id, :date, :name, :fromAccount, :toAccount, :type, :amountInCents)
    """)
    fun insertTransaction(id: String, date: ZonedDateTime, name: String, fromAccount: String, toAccount: String, type: String, amountInCents: Long)

    fun searchTransactions(query: String?, filters: List<List<Pair<String, String>>>): List<Transaction>
}

class TransactionsStoreImpl(db: Jdbi, transactionsStore: TransactionsStore) : TransactionsStore by transactionsStore {

    private val handle = db.open()

    override fun searchTransactions(query: String?, filters: List<List<Pair<String, String>>>): List<Transaction> {
        if (query.isNullOrBlank() && filters.isEmpty()) return emptyList()

        val q = handle.select(
            """
                SELECT t.*,
                (
                   SELECT string_agg(DISTINCT CONCAT_WS(':', key, value), ',' ORDER BY CONCAT_WS(':', key, value))
                   FROM transaction_tags tt
                   WHERE transaction_id = t.id
                ) as tags
                FROM transaction.transactions t
                INNER JOIN transaction.transaction_tags tt ON (t.id = tt.transaction_id)
                WHERE 1=1
                ${query?.isNotBlank()?.let { "AND t.name LIKE '%:query%'" } ?: ""}
                ${filters.takeIf { it.isNotEmpty() }?.let { " AND (" } ?: ""}
                ${filters.map { filter ->
                    "(1=1 ${filter.map { (k, v) -> "AND (tt.key='$k' AND tt.value='$v')"}} OR )"
                }}
                ${filters.takeIf { it.isNotEmpty() }?.let { "1=2 )" } ?: ""}
                ORDER BY t.date DESC
            """.trimIndent()
        )
        if (query?.isNotBlank() == true) q.bind("query", query)
        return q.mapTo(Transaction::class.java).list()
    }
}