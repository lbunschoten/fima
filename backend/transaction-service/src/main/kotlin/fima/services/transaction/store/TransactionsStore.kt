package fima.services.transaction.store

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.time.ZonedDateTime

interface TransactionsStore {

    data class SearchFilters(
        val queryFilter: String?,
        val tagFilters: Map<String, String>
    )

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

    fun searchTransactions(filters: List<SearchFilters>): List<Transaction> = emptyList()
}

class TransactionsStoreImpl(db: Jdbi, transactionsStore: TransactionsStore) : TransactionsStore by transactionsStore {

    private val handle = db.open()

    override fun searchTransactions(filters: List<TransactionsStore.SearchFilters>): List<Transaction> {
        if (filters.isEmpty()) return emptyList()

        val searchQuery = """
                SELECT t.*,
                (
                   SELECT string_agg(DISTINCT CONCAT_WS(':', key, value), ',' ORDER BY CONCAT_WS(':', key, value))
                   FROM transaction.transaction_tags tt
                   WHERE transaction_id = t.id
                ) as tags
                FROM transaction.transactions t
                INNER JOIN transaction.transaction_tags tt ON (t.id = tt.transaction_id)
                WHERE 
                ${
                    filters.joinToString(" OR ") { filter ->
                        val f: List<String?> = 
                            filter.tagFilters.map { (k, v) -> "(tt.key='$k' AND tt.value='$v')" } +
                            (filter.queryFilter?.let { q -> "t.name LIKE '%' || '$q' || '%'" })
                        
                        "(${f.filterNotNull().joinToString(" AND ")})"
                    }
                }
                ORDER BY t.date DESC
            """.trimIndent()

        val q = handle.select(searchQuery)
        return q.registerRowMapper(TransactionRowMapper()).mapTo(Transaction::class.java).list()
    }
}