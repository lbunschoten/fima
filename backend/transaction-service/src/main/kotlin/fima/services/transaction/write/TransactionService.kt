package fima.services.transaction.write

import fima.services.transaction.store.Transaction
import fima.services.transaction.store.TransactionsStore
import java.time.LocalDate
import java.util.*

class TransactionService(
    private val transactionsStore: TransactionsStore
) {

    fun insertTransaction(id: UUID, date: LocalDate, name: String, fromAccountNumber: String, toAccountNumber: String, type: String, amountInCents: Long) {
        transactionsStore.insertTransaction(
            id = id.toString(),
            date = date,
            name = name,
            fromAccount = fromAccountNumber,
            toAccount = toAccountNumber,
            type = type,
            amountInCents = amountInCents
        )
    }

    fun searchTransactions(filters: List<TransactionsStore.SearchFilters>): List<Transaction> {
        return transactionsStore.searchTransactions(filters)
    }

}