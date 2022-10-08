package fima.services.transaction.store


import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.sql.ResultSet

class MonthlyTransactionStatisticsRowMapperSpec : StringSpec() {

    init {
        val mapper = MonthlyTransactionStatisticsRowMapper()
        val resultSet = mockk<ResultSet> {
            every { getInt("month") } returns 10
            every { getInt("year") } returns 2020
            every { getInt("num_transactions") } returns 20
            every { getLong("sum") } returns 30
            every { getLong("balance") } returns 40
        }

        "it should map a transaction statistics row" {
            mapper.map(resultSet, mockk()) shouldBe MonthlyTransactionStatistics(
                month = 10,
                year = 2020,
                numTransactions = 20,
                sum = 30,
                balance = 40
            )
        }

    }

}