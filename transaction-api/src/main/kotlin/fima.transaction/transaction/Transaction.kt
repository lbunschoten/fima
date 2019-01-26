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
        fima.domain.transaction.TransactionType.WIRE_TRANSFER -> fima.transaction.transaction.TransactionType.WireTransfer
        fima.domain.transaction.TransactionType.DIRECT_DEBIT -> fima.transaction.transaction.TransactionType.DirectDebit
        fima.domain.transaction.TransactionType.PAYMENT_TERMINAL -> fima.transaction.transaction.TransactionType.PaymentTerminal
        fima.domain.transaction.TransactionType.TRANSFER -> fima.transaction.transaction.TransactionType.Transfer
        fima.domain.transaction.TransactionType.ONLINE_TRANSFER -> fima.transaction.transaction.TransactionType.OnlineTransfer
        fima.domain.transaction.TransactionType.ATM -> fima.transaction.transaction.TransactionType.ATM
        fima.domain.transaction.TransactionType.TRANSER_COLLECTION -> fima.transaction.transaction.TransactionType.TransferCollection
        else -> fima.transaction.transaction.TransactionType.Other
    }
}

fun fima.domain.transaction.Transaction.simple(): Transaction {
    val transactionDate = LocalDate.of(this.date.year, this.date.month, this.date.day).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    return Transaction(this.id, transactionDate, this.type.simple(), this.name, this.description, this.toAccount, this.fromAccount, this.amount)
}

fun fima.services.transactionstatistics.MonthlyStatistics.simple(): TransactionStatistics {
    return TransactionStatistics(this.month, this.year, this.transaction, this.sum)
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
                                 val sum: Float)
