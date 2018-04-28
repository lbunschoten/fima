package fima.services.transaction.conversion

import fima.domain.transaction.TransactionType

class RawTypeToTransactionTypeConverter : (String) -> TransactionType {

    override fun invoke(rawType: String): TransactionType {
        return when (rawType) {
            "AM" -> TransactionType.WIRE_TRANSFER
            "IC" -> TransactionType.DIRECT_DEBIT
            "BA" -> TransactionType.PAYMENT_TERMINAL
            "OV" -> TransactionType.TRANSFER
            "GT" -> TransactionType.ONLINE_TRANSFER
            "GM" -> TransactionType.ATM
            "VZ" -> TransactionType.TRANSER_COLLECTION
            "DV" -> TransactionType.OTHER
            else -> throw UnsupportedOperationException("Unsupported type: $rawType")
        }
    }

}