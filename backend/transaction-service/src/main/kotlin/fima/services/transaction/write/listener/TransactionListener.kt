package fima.services.transaction.write.listener

import fima.services.transaction.write.TransactionService
import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.TransactionEvent

class TransactionListener(private val transactionService: TransactionService) : (Event) -> Unit {

    override fun invoke(event: Event) {
        when (event) {
            is TransactionEvent -> {
                transactionService.insertTransaction(
                    id = event.id,
                    date = event.date,
                    name = event.name,
                    fromAccountNumber = event.fromAccountNumber,
                    toAccountNumber = event.toAccountNumber,
                    type = event.type,
                    amountInCents = event.amountInCents
                )
            }
        }
    }
}