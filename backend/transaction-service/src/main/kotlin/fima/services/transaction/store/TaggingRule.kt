package fima.services.transaction.store

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.util.*

data class TaggingRule(
    val id: UUID,
    val regex: String,
    val tags: List<String>
)

class TaggingRuleMapper : RowMapper<TaggingRule> {
    override fun map(rs: ResultSet, ctx: StatementContext): TaggingRule {
        return TaggingRule(
            id = UUID.fromString(rs.getString("id")),
            regex = rs.getString("regex"),
            tags = rs.getString("tags").split(',')
        )
    }
}