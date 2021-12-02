package fima.services.transaction.conversion

import fima.domain.transaction.RawTransaction
import fima.domain.transaction.Transaction
import fima.domain.transaction.transaction

class RawTransactionToTransactionConverter(
        private val toDateConverter: RawDateToDateConverter,
        private val toTransactionTypeConverter: RawTypeToTransactionTypeConverter
) : (RawTransaction) -> Transaction {

    override fun invoke(rawTransaction: RawTransaction): Transaction {
        return transaction {
            date = toDateConverter(rawTransaction.date)
            name = rawTransaction.name
            fromAccount = rawTransaction.fromAccount
            toAccount = rawTransaction.toAccount
            amount = rawTransaction.amount
            type = toTransactionTypeConverter(rawTransaction.type)
        }
    }

}