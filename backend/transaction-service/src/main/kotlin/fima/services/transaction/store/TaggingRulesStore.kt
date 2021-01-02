package fima.services.transaction.store

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.io.Closeable
import java.sql.ResultSet
import java.util.UUID
import fima.domain.transaction.TaggingRule as ProtoTaggingRule

interface TaggingRuleStore {

    @SqlQuery("SELECT * FROM TransactionTaggingRule")
    @RegisterRowMapper(TaggingRuleMapper::class)
    fun getTaggingRules(): List<TaggingRule>
}

class TaggingRulesStoreImpl(
    private val db: Jdbi
) : TaggingRuleStore by db.onDemand(TaggingRuleStore::class.java), Closeable {

    private val handle = db.open()

    fun storeTaggingRule(regex: String, tags: Set<String>) {
        handle.execute(
            """INSERT INTO TransactionTaggingRule (`id`, `regex`, `tags`)VALUES (?, ?, ?)""",
            UUID.randomUUID().toString(), regex, tags.joinToString(",")
        )
    }

    override fun close() = handle.close()
}

class TaggingRuleMapper : RowMapper<TaggingRule> {
    override fun map(rs: ResultSet, ctx: StatementContext): TaggingRule {
        return TaggingRule(
            id = UUID.fromString(rs.getString("id")),
            regex = rs.getString("regex"),
            tags = rs.getString("tags").split(',')
        )
    }
}

data class TaggingRule(
    val id: UUID,
    val regex: String,
    val tags: List<String>
) {
    companion object {
        fun toProto(taggingRule: TaggingRule): ProtoTaggingRule {
            return ProtoTaggingRule
                .newBuilder()
                .setId(taggingRule.id.toString())
                .setRegex(taggingRule.regex)
                .addAllTags(taggingRule.tags)
                .build()
        }
    }
}
