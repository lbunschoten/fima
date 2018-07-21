package fima.transaction.model

import fima.domain.transaction.{Transaction => ProtoTransaction, TransactionType => ProtoTransactionType}

sealed trait TransactionType

case object WireTransfer extends TransactionType

case object DirectDebit extends TransactionType

case object PaymentTerminal extends TransactionType

case object Transfer extends TransactionType

case object OnlineTransfer extends TransactionType

case object ATM extends TransactionType

case object TransferCollection extends TransactionType

case object Other extends TransactionType

case class Transaction(id: Int,
                       date: String,
                       `type`: TransactionType,
                       name: String,
                       description: String,
                       toAccount: String,
                       fromAccount: String,
                       amount: Float)

object Transaction {

  def fromProto(t: ProtoTransaction): Transaction = {
    Transaction(
      t.getId,
      s"${"%02d".format(t.getDate.getDay)}-${"%02d".format(t.getDate.getMonth)}-${t.getDate.getYear}",
      TransactionType.fromProto(t.getType),
      t.getName,
      t.getDescription,
      t.getToAccount,
      t.getFromAccount,
      t.getAmount
    )
  }

}

object TransactionType {

  def fromProto(`type`: ProtoTransactionType): TransactionType = {
    `type` match {
      case ProtoTransactionType.WIRE_TRANSFER => WireTransfer
      case ProtoTransactionType.DIRECT_DEBIT => DirectDebit
      case ProtoTransactionType.PAYMENT_TERMINAL => PaymentTerminal
      case ProtoTransactionType.TRANSFER => Transfer
      case ProtoTransactionType.ONLINE_TRANSFER => OnlineTransfer
      case ProtoTransactionType.ATM => ATM
      case ProtoTransactionType.TRANSER_COLLECTION => TransferCollection
      case _ => Other
    }
  }

}