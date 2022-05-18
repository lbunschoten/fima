package fima.services.transaction.store

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

data class MonthlyTransactionStatistics(
    val month: Int,
    val year: Int,
    val numTransactions: Int,
    val sum: Long,
    val balance: Long
)

class MonthlyTransactionStatisticsRowMapper : RowMapper<MonthlyTransactionStatistics> {
    override fun map(rs: ResultSet, ctx: StatementContext): MonthlyTransactionStatistics {
        return MonthlyTransactionStatistics(
            month = rs.getInt("month"),
            year = rs.getInt("year"),
            numTransactions = rs.getInt("num_transactions"),
            sum = rs.getLong("sum"),
            balance = rs.getLong("balance")
        )
    }
}
