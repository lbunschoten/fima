package fima.services.transaction.store

import fima.domain.transaction.TaggingRule
import fima.services.utils.ToProtoConvertable
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.util.UUID

data class TaggingRule(
    val id: UUID,
    val regex: String,
    val tags: List<String>
) : ToProtoConvertable<TaggingRule> {
    override fun toProto(): TaggingRule {
        return TaggingRule.newBuilder()
            .setId(id.toString())
            .setRegex(regex)
            .addAllTags(tags)
            .build()
    }
}

class TaggingRuleMapper : RowMapper<fima.services.transaction.store.TaggingRule> {
    override fun map(rs: ResultSet, ctx: StatementContext): fima.services.transaction.store.TaggingRule {
        return TaggingRule(
            id = UUID.fromString(rs.getString("id")),
            regex = rs.getString("regex"),
            tags = rs.getString("tags").split(',')
        )
    }
}