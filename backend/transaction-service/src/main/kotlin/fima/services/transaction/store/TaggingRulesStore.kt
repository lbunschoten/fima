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

    fun storeTaggingRule(taggingRule: ProtoTaggingRule) {
        handle
            .createUpdate("""
                INSERT INTO TransactionTaggingRule (`id`, `regex`, `tags`) VALUES (:transaction_id, :regex, :tags)
                ON DUPLICATE KEY UPDATE `regex` = :regex, tags = :tags
            """)
            .bind(":transaction_id", taggingRule.id?.toString() ?: UUID.randomUUID().toString())
            .bind(":regex", taggingRule.regex)
            .bind(":tags", taggingRule.tagsList.toSet())
            .execute()
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
) : ToProtoConvertable<ProtoTaggingRule> {
    override fun toProto(): ProtoTaggingRule {
        return ProtoTaggingRule
            .newBuilder()
            .setId(id.toString())
            .setRegex(regex)
            .addAllTags(tags)
            .build()
    }
}

interface ToProtoConvertable<P> {
    fun toProto(): P
}

interface FromProtoConvertable<D> {
    fun fromProto(): D
}

object ProtoUtils {

    fun <D> Collection<FromProtoConvertable<D>>.fromProto() = this.map { it.fromProto() }
    fun <P> Collection<ToProtoConvertable<P>>.toProto() = this.map { it.toProto() }

}