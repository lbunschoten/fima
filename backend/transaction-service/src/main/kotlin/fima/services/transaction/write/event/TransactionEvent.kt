package fima.services.transaction.write.event

import java.time.LocalDate
import java.util.*

interface TransactionEvent {

    val id: UUID
    val amountInCents: Long
    val date: LocalDate
    val name: String
    val details: String
    val fromAccountNumber: String
    val toAccountNumber: String
    val type: String

    val amountInCentsDiff: Long

    fun toFullString(): String {
        return "amount=${amountInCents}/date=${date}/name=${name}/detals=$details/from=$fromAccountNumber/to=$toAccountNumber/type=$type"
    }
}