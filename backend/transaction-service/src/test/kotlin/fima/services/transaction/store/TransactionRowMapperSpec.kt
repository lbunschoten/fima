package fima.services.transaction.store


import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.sql.Date
import java.sql.ResultSet
import java.time.LocalDate
import java.util.*

class TransactionRowMapperSpec : StringSpec() {

    init {
        val mapper = TransactionRowMapper()
        val id = UUID.randomUUID()
        val resultSet = mockk<ResultSet> {
            every { getString("id") } returns id.toString()
            every { getString("type") } returns "GM"
            every { getDate("date") } returns Date(0)
            every { getString("name") } returns "name"
            every { getString("from_account") } returns "from_account"
            every { getString("to_account") } returns "to_account"
            every { getFloat("amount") } returns 1.0F
            every { getString("tags") } returns "k1:v1,k2:v2"
        }

        "it should map a transaction row" {
            mapper.map(resultSet, mockk()) shouldBe Transaction(
                id = id,
                type = TransactionType.ATM,
                date = LocalDate.ofEpochDay(0),
                name = "name",
                fromAccount = "from_account",
                toAccount = "to_account",
                amount = 1.0F,
                tags = mapOf(Pair("k1", "v1"), Pair("k2", "v2"))
            )
        }

    }

}