package fima.services.transaction.store


import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.sql.ResultSet
import java.util.*

class TaggingRuleRowMapperSpec : StringSpec() {

    init {
        val mapper = TaggingRuleRowMapper()
        val id = UUID.randomUUID()
        val resultSet = mockk<ResultSet> {
            every { getString("id") } returns id.toString()
            every { getString("regex") } returns "regex"
            every { getString("tags") } returns "tag1,tag2"
        }

        "it should map a tagging rule row" {
            mapper.map(resultSet, mockk()) shouldBe TaggingRule(
                id = id,
                regex = "regex",
                tags = listOf("tag1", "tag2")
            )
        }

    }

}