package fima.services.transaction.store

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.util.Optional

class TransactionStatisticsStoreImpl(
    private val db: Jdbi
) : TransactionStatisticsStore by db.onDemand(TransactionStatisticsStore::class.java) {

    fun insertTransaction(month: Int, year: Int, amountInCents: Long) {
        val statistics = getStatistics(month, year).orElse(null)
        statistics?.let {
            updateStatistic(
                numTransactions = statistics.numTransactions + 1,
                sum = statistics.sum + amountInCents,
                balance = statistics.balance + amountInCents,
                month = month,
                year = year
            )
        } ?: run {
            val lastAvailableStatistics = getLastAvailableStatistics(month, year)
            insertStatistic(
                sum = amountInCents,
                balance = (lastAvailableStatistics.orElse(null)?.balance ?: 0) + amountInCents,
                month = month,
                year = year
            )
        }
    }

    private fun insertStatistic(sum: Long, balance: Long, month: Int, year: Int) {
        db.withHandleUnchecked { handle ->
            handle.execute("""
              INSERT INTO monthly_transaction_statistics (month, year, num_transactions, sum, balance)
              VALUES (?, ?, 1, ?, ?)
            """, month, year, sum, balance)
        }
    }

    private fun updateStatistic(numTransactions: Int, sum: Long, balance: Long, month: Int, year: Int) {
        db.withHandleUnchecked {handle ->
            handle.execute("""
                UPDATE monthly_transaction_statistics
                SET 
                  num_transactions = ?,
                  sum = ?,
                  balance = ?
                WHERE month = ? AND year = ?
                """,
                    numTransactions, sum, balance, month, year
                )
        }
    }
}

interface TransactionStatisticsStore {

    @SqlQuery("""
        SELECT *
        FROM monthly_transaction_statistics
        WHERE month = :month
        AND year = :year
    """)
    @RegisterRowMapper(MonthlyTransactionStatisticsRowMapper::class)
    fun getStatistics(month: Int, year: Int): Optional<MonthlyTransactionStatistics>

    @SqlQuery("""
        SELECT *
        FROM monthly_transaction_statistics
        WHERE ((month >= :startMonth AND year >= :startYear) OR year > :startYear)
        AND ((month <= :endMonth AND year <= :endYear) OR year < :endYear) 
        ORDER BY year, month
    """)
    @RegisterRowMapper(MonthlyTransactionStatisticsRowMapper::class)
    fun getMonthlyStatistics(startMonth: Int, startYear: Int, endMonth: Int, endYear: Int): List<MonthlyTransactionStatistics>

    @SqlQuery("""
        SELECT *
        FROM monthly_transaction_statistics
        WHERE CONCAT(year,TO_CHAR(month, 'fm00')) < CONCAT(:year, :month)
        ORDER BY CONCAT(year,TO_CHAR(month, 'fm00')) DESC
        LIMIT 1
    """)
    @RegisterRowMapper(MonthlyTransactionStatisticsRowMapper::class)
    fun getLastAvailableStatistics(month: Int, year: Int): Optional<MonthlyTransactionStatistics>

}