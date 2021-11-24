package fima.services.transaction.write

import fima.services.transaction.store.BankAccountEventStore
import fima.services.transaction.store.TaggingRule
import fima.services.transaction.store.TaggingRulesStoreImpl
import fima.services.transaction.store.TransactionTagsStore
import fima.services.transaction.write.event.TransactionEvent
import java.util.regex.Pattern

class TaggingService(
    private val bankAccountEventStore: BankAccountEventStore,
    private val taggingRulesStoreImpl: TaggingRulesStoreImpl,
    private val transactionTagsStore: TransactionTagsStore
) {

    fun tagTransactions() {
        transactionTagsStore.deleteTags()

        bankAccountEventStore
            .aggregates()
            .forEach(this::tagTransactionsForBankAccount)
    }

    fun tagTransaction(transactionEvent: TransactionEvent) {
        val taggingRules = taggingRulesStoreImpl.getTaggingRules()
        addTagsForTransaction(taggingRules, transactionEvent)
    }

    private fun tagTransactionsForBankAccount(bankAccount: String) {
        val events = bankAccountEventStore.readEvents(bankAccount)
        val taggingRules = taggingRulesStoreImpl.getTaggingRules()

        events
            .filterIsInstance<TransactionEvent>()
            .forEach { addTagsForTransaction(taggingRules, it) }
    }

    private fun addTagsForTransaction(taggingRules: List<TaggingRule>, event: TransactionEvent) {
        val tags = taggingRules.flatMap { taggingRule ->
            val p = Pattern.compile(taggingRule.regex)
            val matcher = p.matcher(event.toFullString())

            if (matcher.matches()) {
                taggingRule.tags
            } else {
                emptyList()
            }
        }.associate {
            val (key, value) = it.split(":", limit = 2)
            Pair(key, value)
        }

        if (tags.isNotEmpty()) {
            transactionTagsStore.storeTags(event.id, tags)
        }
    }

}