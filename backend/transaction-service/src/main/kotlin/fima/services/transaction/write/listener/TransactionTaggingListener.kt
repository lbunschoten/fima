package fima.services.transaction.write.listener

import fima.services.transaction.store.TaggingRulesStoreImpl
import fima.services.transaction.store.TransactionTagsStore
import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.TransactionEvent
import java.util.regex.Pattern

class TransactionTaggingListener(
    private val transactionTagsStore: TransactionTagsStore,
    private val taggingRulesStore: TaggingRulesStoreImpl
) : (Event) -> Unit {

    override fun invoke(event: Event) {
        if (event is TransactionEvent) {
            addTagsForTransaction(event)
        }
    }

    private fun addTagsForTransaction(event: TransactionEvent) {
        val taggingRules = taggingRulesStore.getTaggingRules()

        val tags = taggingRules.flatMap { taggingRule ->
            val p = Pattern.compile(taggingRule.regex)
            val matcher = p.matcher(event.toFullString())

            if (matcher.matches()) {
                taggingRule.tags
            } else {
                emptyList()
            }
        }.map {
            val (key, value) = it.split(":", limit = 2)
            Pair(key, value)
        }.toMap()

        transactionTagsStore.storeTags(event.id, tags)
    }

}
