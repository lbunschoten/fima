package fima.services.transaction.store

import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.util.Optional

interface TransactionStatisticsStore {

  @SqlQuery("""
    SELECT *
    FROM MonthlyTransactionStatistics
    WHERE month = :month
    AND year = :year
  """)
  @RegisterRowMapper(MonthlyTransactionStatisticsRowMapper::class)
  fun getStatistics(month: Int, year: Int): Optional<MonthlyTransactionStatistics>

  fun getPreviousMonthStatistics(month: Int, year: Int): MonthlyTransactionStatistics? {
    return if (month == 1) getStatistics(12, year - 1).orElse(null)
    else getStatistics(month - 1, year).orElse(null)
  }

}