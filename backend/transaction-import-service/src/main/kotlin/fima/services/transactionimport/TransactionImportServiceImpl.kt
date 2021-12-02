package fima.services.transactionimport

import com.opencsv.bean.CsvBindByPosition
import com.opencsv.bean.CsvToBeanBuilder
import fima.services.transaction.*
import org.slf4j.LoggerFactory
import java.io.StringReader

class TransactionImportServiceImpl(private val transactionService: TransactionServiceGrpcKt.TransactionServiceCoroutineStub) : TransactionImportServiceGrpcKt.TransactionImportServiceCoroutineImplBase() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun importTransactions(request: ImportTransactionsRequest): ImportTransactionsResponse {
        StringReader(request.transactions).use { reader ->
            val csvReader = CsvToBeanBuilder<Transaction>(reader)
                .withSeparator(',')
                .withQuoteChar('\"')
                .withType(Transaction::class.java)
                .withThrowExceptions(true)
                .withSkipLines(1)
                .build()

            val transactions = csvReader.parse()
            logger.info("Importing ${transactions.size} transactions")

            transactions.forEachIndexed { index, transaction ->
                if (index == 0) {
                    transactionService.openBankAccount(
                        openBankAccountRequest {
                            accountNumber = if (transaction.direction == "Af") transaction.firstAccount else transaction.secondAccount
                            initialBalance = 5000F // FIXME: Make configurable
                        }
                    )
                }

                if (transaction.direction == "Af") {
                    transactionService.withdraw(
                        withdrawRequest {
                            amountInCents = (transaction.amount.replace(',', '.').toFloat() * 100).toLong()
                            date = transaction.date
                            details = transaction.details
                            name = transaction.name
                            fromAccount = transaction.firstAccount
                            toAccount = transaction.secondAccount
                            type = transaction.type
                        }
                    )
                } else {
                    transactionService.deposit(
                        depositRequest {
                            amountInCents = (transaction.amount.replace(',', '.').toFloat() * 100).toLong()
                            date = transaction.date
                            details = transaction.details
                            name = transaction.name
                            fromAccount = transaction.secondAccount
                            toAccount = transaction.firstAccount
                            type = transaction.type
                        }
                    )
                }

            }
        }

        return importTransactionsResponse {}
    }
}

data class Transaction(
    @CsvBindByPosition(position = 0) val date: Int = 0,
    @CsvBindByPosition(position = 1) val name: String = "",
    @CsvBindByPosition(position = 2) val firstAccount: String = "",
    @CsvBindByPosition(position = 3) val secondAccount: String = "",
    @CsvBindByPosition(position = 4) val type: String = "",
    @CsvBindByPosition(position = 5) val direction: String = "",
    @CsvBindByPosition(position = 6) val amount: String = "",
    @CsvBindByPosition(position = 8) val details: String = ""
)
