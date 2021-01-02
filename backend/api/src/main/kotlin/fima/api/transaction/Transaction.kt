package fima.api.transaction

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import fima.domain.transaction.MonthlyTransactionStatistics as ProtoMonthlyTransactionStatistics
import fima.domain.transaction.Transaction as ProtoTransaction
import fima.domain.transaction.TransactionType as ProtoTransactionType

enum class TransactionType {
    WireTransfer,
    DirectDebit,
    PaymentTerminal,
    Transfer,
    OnlineTransfer,
    ATM,
    TransferCollection,
    Other;
}

fun ProtoTransactionType.simple(): TransactionType {
    return when (this) {
        ProtoTransactionType.WIRE_TRANSFER -> TransactionType.WireTransfer
        ProtoTransactionType.DIRECT_DEBIT -> TransactionType.DirectDebit
        ProtoTransactionType.PAYMENT_TERMINAL -> TransactionType.PaymentTerminal
        ProtoTransactionType.TRANSFER -> TransactionType.Transfer
        ProtoTransactionType.ONLINE_TRANSFER -> TransactionType.OnlineTransfer
        ProtoTransactionType.ATM -> TransactionType.ATM
        ProtoTransactionType.TRANSER_COLLECTION -> TransactionType.TransferCollection
        else -> TransactionType.Other
    }
}

fun ProtoTransaction.simple(): Transaction {
    val transactionDate = LocalDate.of(this.date.year, this.date.month, this.date.day).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    return Transaction(UUID.fromString(this.id), transactionDate, this.type.simple(), this.name, this.description, this.toAccount, this.fromAccount, this.amount)
}

fun ProtoMonthlyTransactionStatistics.simple(): MonthlyTransactionStatistics {
    return MonthlyTransactionStatistics(this.month, this.year, this.transaction, this.sum, this.balance)
}

data class Transaction(
    val id: UUID,
    val date: String,
    val type: TransactionType,
    val name: String,
    val description: String,
    val toAccount: String,
    val fromAccount: String,
    val amount: Float
)

data class MonthlyTransactionStatistics(
    val month: Int,
    val year: Int,
    val transactions: Int,
    val sum: Float,
    val balance: Float
)
