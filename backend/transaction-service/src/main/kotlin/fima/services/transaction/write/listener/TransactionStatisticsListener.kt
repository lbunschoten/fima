package fima.services.transaction.write.listener

import fima.services.transaction.write.TransactionStatisticsService
import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.TransactionEvent

class TransactionStatisticsListener(
    private val transactionStatisticsService: TransactionStatisticsService
) : (Event) -> Unit {

    override fun invoke(event: Event) {
        when (event) {
            is TransactionEvent -> {
                transactionStatisticsService.addTransaction(event.date, event.amountInCentsDiff)
            }
        }
    }

}