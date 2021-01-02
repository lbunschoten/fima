package fima.services.transaction.store

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import fima.domain.transaction.MonthlyTransactionStatistics as ProtoMonthlyTransactionStatistics

data class MonthlyTransactionStatistics(
    val month: Int,
    val year: Int,
    val numTransactions: Int,
    val sum: Long,
    val balance: Long
): ToProtoConvertable<ProtoMonthlyTransactionStatistics> {

    companion object: RowMapper<MonthlyTransactionStatistics> {
        override fun map(rs: ResultSet, ctx: StatementContext): MonthlyTransactionStatistics {
            return MonthlyTransactionStatistics(
                month = rs.getInt("month"),
                year = rs.getInt("year"),
                numTransactions = rs.getInt("numTransactions"),
                sum = rs.getLong("sum"),
                balance = rs.getLong("balance")
            )
        }
    }

    override fun toProto(): ProtoMonthlyTransactionStatistics {
        return ProtoMonthlyTransactionStatistics
            .newBuilder()
            .setMonth(month)
            .setYear(year)
            .setTransaction(numTransactions)
            .setSum(sum.toFloat())
            .setBalance(balance.toFloat())
            .build()
    }

}
