package fima.services.transaction.read.store

import fima.services.transaction.store.MonthlyTransactionStatistics
import fima.services.transaction.store.MonthlyTransactionStatisticsRowMapper
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery

interface  TransactionStatisticsStore {

  @SqlQuery("""
    SELECT *
    FROM MonthlyTransactionStatistics
    WHERE ((month >= :startMonth AND year >= :startYear) OR year > startYear)
    AND ((month <= :endMonth AND year <= :endYear) OR year < :endYear) 
    ORDER BY year, month
  """)
  @RegisterRowMapper(MonthlyTransactionStatisticsRowMapper::class)
  fun getMonthlyStatistics(startMonth: Int, startYear: Int, endMonth: Int, endYear: Int): List<MonthlyTransactionStatistics>

}
