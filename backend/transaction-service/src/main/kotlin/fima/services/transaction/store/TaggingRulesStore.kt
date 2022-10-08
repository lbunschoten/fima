package fima.services.transaction.store

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery

interface TaggingRuleStore {

    @SqlQuery("SELECT * FROM transaction_tagging_rule")
    @RegisterRowMapper(TaggingRuleRowMapper::class)
    fun getTaggingRules(): List<TaggingRule>
}

class TaggingRulesStoreImpl(
    private val db: Jdbi
) : TaggingRuleStore by db.onDemand(TaggingRuleStore::class.java)



