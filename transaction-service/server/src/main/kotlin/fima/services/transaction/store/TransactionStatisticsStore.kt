package fima.services.transaction.store

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet
import java.util.Optional

interface TransactionStatisticsStore {

  private class StatisticsRowMapper : RowMapper<MonthlyTransactionStatistics> {
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

  @SqlQuery("""
    SELECT *
    FROM MonthlyTransactionStatistics
    WHERE month = :month
    AND year = :year
  """)
  @RegisterRowMapper(StatisticsRowMapper::class)
  fun getStatistics(month: Int, year: Int): Optional<MonthlyTransactionStatistics>

  fun getPreviousMonthStatistics(month: Int, year: Int): MonthlyTransactionStatistics? {
    return if (month == 1) getStatistics(12, year - 1).orElse(null)
    else getStatistics(month - 1, year).orElse(null)
  }

}