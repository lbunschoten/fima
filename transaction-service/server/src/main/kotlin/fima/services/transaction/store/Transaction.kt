package fima.services.transaction.store

import fima.domain.transaction.Date
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.time.LocalDate
import java.util.UUID
import fima.domain.transaction.Transaction as ProtoTransaction
import fima.domain.transaction.TransactionType as ProtoTransactionType

data class Transaction(
  val id: UUID,
  val type: TransactionType,
  val date: LocalDate,
  val name: String,
  val fromAccount: String,
  val toAccount: String,
  val amount: Float
) {

  fun toProto(): ProtoTransaction {
    return ProtoTransaction
      .newBuilder()
      .setId(id.toString())
      .setType(type.toProto())
      .setDate(Date.newBuilder().setDay(date.dayOfMonth).setMonth(date.monthValue).setYear(date.year).build())
      .setName(name)
      .setFromAccount(fromAccount)
      .setToAccount(toAccount)
      .setAmount(amount)
      .build()
  }

}

enum class TransactionType(private val abbreviation: String) {
  WireTransfer("AM"),
  DirectDebit("IC"),
  PaymentTerminal("BA"),
  Transfer("OV"),
  OnlineTransfer("GT"),
  ATM("GM"),
  TransferCollection("VZ"),
  Other("DV");

  companion object {
    fun of(abbreviation: String): TransactionType {
      return when (abbreviation) {
        "AM" -> WireTransfer
        "IC" -> DirectDebit
        "BA" -> PaymentTerminal
        "OV" -> Transfer
        "GT" -> OnlineTransfer
        "GM" -> ATM
        "VZ" -> TransferCollection
        "DV" -> Other
        else -> throw IllegalArgumentException("Argument contained an unsupported transaction type")
      }
    }
  }

  fun toProto(): ProtoTransactionType {
    return when (this) {
      WireTransfer -> ProtoTransactionType.WIRE_TRANSFER
      DirectDebit -> ProtoTransactionType.DIRECT_DEBIT
      PaymentTerminal -> ProtoTransactionType.PAYMENT_TERMINAL
      Transfer -> ProtoTransactionType.TRANSFER
      OnlineTransfer -> ProtoTransactionType.ONLINE_TRANSFER
      ATM -> ProtoTransactionType.ATM
      TransferCollection -> ProtoTransactionType.TRANSER_COLLECTION
      Other -> ProtoTransactionType.OTHER
    }
  }

  override fun toString(): String = abbreviation
}

class TransactionMapper : RowMapper<Transaction> {
  override fun map(rs: ResultSet, ctx: StatementContext): Transaction {
    return Transaction(
      id = UUID.fromString(rs.getString("id")),
      type = TransactionType.of(rs.getString("type")),
      date = rs.getDate("date").toLocalDate(),
      name = rs.getString("name"),
      fromAccount = rs.getString("from_account"),
      toAccount = rs.getString("to_account"),
      amount = rs.getFloat("amount")
    )
  }
}