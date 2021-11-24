package fima.services.transaction.store

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.io.Closeable
import java.util.UUID
import fima.domain.transaction.TaggingRule as ProtoTaggingRule

interface TaggingRuleStore {

    @SqlQuery("SELECT * FROM transaction_tagging_rule")
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
                INSERT INTO transaction_tagging_rule (id, regex, tags) VALUES (:transaction_id, :regex, :tags)
                ON CONFLICT(id) DO UPDATE SET 
                    regex = :regex, 
                    tags = :tags
            """)
            .bind("transaction_id", taggingRule.id?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString())
            .bind("regex", taggingRule.regex)
            .bind("tags", taggingRule.tagsList.toSet().joinToString(","))
            .execute()
    }

    override fun close() = handle.close()
}



