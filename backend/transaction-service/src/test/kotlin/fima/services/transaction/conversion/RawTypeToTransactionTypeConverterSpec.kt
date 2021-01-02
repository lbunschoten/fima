package fima.services.transaction.conversion

import fima.domain.transaction.TransactionType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RawTypeToTransactionTypeConverterSpec : StringSpec() {

    init {

        val converter = RawTypeToTransactionTypeConverter()

        "it should convert all transaction types" {
            converter("AM") shouldBe TransactionType.WIRE_TRANSFER
            converter("IC") shouldBe TransactionType.DIRECT_DEBIT
            converter("BA") shouldBe TransactionType.PAYMENT_TERMINAL
            converter("OV") shouldBe TransactionType.TRANSFER
            converter("GT") shouldBe TransactionType.ONLINE_TRANSFER
            converter("GM") shouldBe TransactionType.ATM
            converter("VZ") shouldBe TransactionType.TRANSER_COLLECTION
            converter("DV") shouldBe TransactionType.OTHER
        }

        "it should throw an UnsupportedOperationException when converting an unknown type" {
            val exception = shouldThrow<UnsupportedOperationException> {
                converter("UNKNOWN")
            }
            exception.message shouldBe "Unsupported type: UNKNOWN"
        }

    }

}