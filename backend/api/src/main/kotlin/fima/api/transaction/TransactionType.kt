package fima.api.transaction

import fima.api.utils.FromProtoConvertable

enum class TransactionType {
    WireTransfer,
    DirectDebit,
    PaymentTerminal,
    Transfer,
    OnlineTransfer,
    ATM,
    TransferCollection,
    Other;

    companion object: FromProtoConvertable<fima.domain.transaction.TransactionType, TransactionType> {
        override fun fromProto(proto: fima.domain.transaction.TransactionType): TransactionType {
            return when (proto) {
                fima.domain.transaction.TransactionType.WIRE_TRANSFER -> WireTransfer
                fima.domain.transaction.TransactionType.DIRECT_DEBIT -> DirectDebit
                fima.domain.transaction.TransactionType.PAYMENT_TERMINAL -> PaymentTerminal
                fima.domain.transaction.TransactionType.TRANSFER -> Transfer
                fima.domain.transaction.TransactionType.ONLINE_TRANSFER -> OnlineTransfer
                fima.domain.transaction.TransactionType.ATM -> ATM
                fima.domain.transaction.TransactionType.TRANSER_COLLECTION -> TransferCollection
                else -> Other
            }
        }
    }
}