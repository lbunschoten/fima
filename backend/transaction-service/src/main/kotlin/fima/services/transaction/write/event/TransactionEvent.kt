package fima.services.transaction.write.event

import java.util.UUID

interface TransactionEvent {

    val id: UUID
    val amountInCents: Long
    val date: Int
    val name: String
    val details: String
    val fromAccountNumber: String
    val toAccountNumber: String
    val type: String

    fun toFullString(): String {
        return "amount=${amountInCents}/date=${date}/name=${name}/detals=$details/from=$fromAccountNumber/to=$toAccountNumber/type=$type"
    }
}