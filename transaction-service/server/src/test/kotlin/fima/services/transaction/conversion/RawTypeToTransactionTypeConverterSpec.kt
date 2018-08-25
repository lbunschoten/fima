package fima.services.transaction.conversion

import fima.domain.transaction.TransactionType
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

class RawTypeToTransactionTypeConverterSpec : StringSpec() {

  override fun isInstancePerTest(): Boolean = true

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