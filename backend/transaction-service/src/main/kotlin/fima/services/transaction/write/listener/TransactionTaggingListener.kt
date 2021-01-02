package fima.services.transaction.write.listener

import fima.services.transaction.write.TaggingService
import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.TransactionEvent

class TransactionTaggingListener(
    private val taggingService: TaggingService
) : (Event) -> Unit {

    override fun invoke(event: Event) {
        if (event is TransactionEvent) {
            taggingService.tagTransaction(event)
        }
    }



}
