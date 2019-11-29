package fima.transaction.transaction

import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

fun fima.domain.transaction.TransactionType.simple(): TransactionType {
    return when (this) {
        fima.domain.transaction.TransactionType.WIRE_TRANSFER -> TransactionType.WireTransfer
        fima.domain.transaction.TransactionType.DIRECT_DEBIT -> TransactionType.DirectDebit
        fima.domain.transaction.TransactionType.PAYMENT_TERMINAL -> TransactionType.PaymentTerminal
        fima.domain.transaction.TransactionType.TRANSFER -> TransactionType.Transfer
        fima.domain.transaction.TransactionType.ONLINE_TRANSFER -> TransactionType.OnlineTransfer
        fima.domain.transaction.TransactionType.ATM -> TransactionType.ATM
        fima.domain.transaction.TransactionType.TRANSER_COLLECTION -> TransactionType.TransferCollection
        else -> TransactionType.Other
    }
}

fun fima.domain.transaction.Transaction.simple(): Transaction {
    val transactionDate = LocalDate.of(this.date.year, this.date.month, this.date.day).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    return Transaction(this.id, transactionDate, this.type.simple(), this.name, this.description, this.toAccount, this.fromAccount, this.amount)
}

fun fima.services.transaction.MonthlyStatistics.simple(): TransactionStatistics {
    return TransactionStatistics(this.month, this.year, this.transaction, this.sum, this.balance)
}

data class Transaction(val id: Int,
                       val date: String,
                       val type: TransactionType,
                       val name: String,
                       val description: String,
                       val toAccount: String,
                       val fromAccount: String,
                       val amount: Float)

data class TransactionStatistics(val month: Int,
                                 val year: Int,
                                 val transactions: Int,
                                 val sum: Float,
                                 val balance: Float)
