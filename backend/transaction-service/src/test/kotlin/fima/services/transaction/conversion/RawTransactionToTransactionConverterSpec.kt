package fima.services.transaction.conversion

import fima.domain.transaction.Date
import fima.domain.transaction.RawTransaction
import fima.domain.transaction.Transaction
import fima.domain.transaction.TransactionType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class RawTransactionToTransactionConverterSpec : StringSpec() {

    init {

        val rawDataConverter = mockk<RawDateToDateConverter>()
        val toTransactionTypeConverter = mockk<RawTypeToTransactionTypeConverter>()
        val converter = RawTransactionToTransactionConverter(rawDataConverter, toTransactionTypeConverter)

        "it should convert a rawTransaction to a transaction" {
            val transactionDate = Date.newBuilder().setDay(19).setMonth(7).setYear(2018).build()
            every { rawDataConverter(20180713) } returns transactionDate
            every { toTransactionTypeConverter("ATM") } returns TransactionType.ATM

            converter(RawTransaction.newBuilder().run {
                amount = 1.20F
                date = 20180713
                name = "name"
                details = "details"
                fromAccount = "from"
                toAccount = "to"
                type = "ATM"
                build()
            }) shouldBe Transaction.newBuilder().run {
                amount = 1.20F
                date = transactionDate
                name = "name"
                fromAccount = "from"
                toAccount = "to"
                type = TransactionType.ATM
                build()
            }
        }

    }

}