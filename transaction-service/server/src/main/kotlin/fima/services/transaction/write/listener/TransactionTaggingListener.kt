package fima.services.transaction.write.listener

import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyWithdrawnEvent
import fima.services.transaction.write.store.TransactionTagsWritesStore

class TransactionTaggingListener(private val transactionTagsWritesStore: TransactionTagsWritesStore) : (Event) -> Unit {

  override fun invoke(event: Event) {
    when(event) {
      is MoneyWithdrawnEvent -> {
        val tags = tagWithdrawal(event)
        transactionTagsWritesStore.storeTags(event.id, tags)
      }
    }
  }

  private fun tagWithdrawal(event: MoneyWithdrawnEvent): Map<String, String> {
    if (event.name.contains("hypotheken")) {
        return mapOf(
            "tag1" to "tag1-value",
            "tag2" to "tag2-value"
        )
    }

    return emptyMap()
  }

}