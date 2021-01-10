package fima.services.transaction.store

import fima.services.utils.ToProtoConvertable

enum class TransactionType(private val abbreviation: String): ToProtoConvertable<fima.domain.transaction.TransactionType> {
    WireTransfer("AM"),
    DirectDebit("IC"),
    PaymentTerminal("BA"),
    Transfer("OV"),
    OnlineTransfer("GT"),
    ATM("GM"),
    TransferCollection("VZ"),
    Ideal("ID"),
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
                "ID" -> Ideal
                "DV" -> Other
                else -> throw IllegalArgumentException("Argument contained an unsupported transaction type")
            }
        }
    }

    override fun toProto(): fima.domain.transaction.TransactionType {
        return when (this) {
            WireTransfer -> fima.domain.transaction.TransactionType.WIRE_TRANSFER
            DirectDebit -> fima.domain.transaction.TransactionType.DIRECT_DEBIT
            PaymentTerminal -> fima.domain.transaction.TransactionType.PAYMENT_TERMINAL
            Transfer -> fima.domain.transaction.TransactionType.TRANSFER
            OnlineTransfer -> fima.domain.transaction.TransactionType.ONLINE_TRANSFER
            ATM -> fima.domain.transaction.TransactionType.ATM
            TransferCollection -> fima.domain.transaction.TransactionType.TRANSER_COLLECTION
            Ideal -> fima.domain.transaction.TransactionType.ONLINE_TRANSFER
            Other -> fima.domain.transaction.TransactionType.OTHER
        }
    }

    override fun toString(): String = abbreviation
}