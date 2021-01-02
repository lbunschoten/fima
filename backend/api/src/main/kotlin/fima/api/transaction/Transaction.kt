package fima.api.transaction

import fima.api.utils.FromProtoConvertable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import fima.domain.transaction.Transaction as ProtoTransaction

data class Transaction(
    val id: UUID,
    val date: String,
    val type: TransactionType,
    val name: String,
    val description: String,
    val toAccount: String,
    val fromAccount: String,
    val amount: Float
) {
    companion object: FromProtoConvertable<ProtoTransaction, Transaction> {
        override fun fromProto(proto: ProtoTransaction): Transaction {
            val transactionDate = LocalDate.of(proto.date.year, proto.date.month, proto.date.day).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            return Transaction(UUID.fromString(proto.id), transactionDate, proto.type.let(TransactionType::fromProto), proto.name, proto.description, proto.toAccount, proto.fromAccount, proto.amount)
        }
    }
}

