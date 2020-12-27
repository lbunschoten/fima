package fima.services.transactionimport

import com.opencsv.bean.CsvBindByPosition
import com.opencsv.bean.CsvToBeanBuilder
import fima.services.transaction.write.DepositRequest
import fima.services.transaction.write.OpenBankAccountRequest
import fima.services.transaction.write.TransactionWritesServiceGrpcKt
import fima.services.transaction.write.WithdrawRequest
import org.slf4j.LoggerFactory
import java.io.StringReader

class TransactionImportServiceImpl(private val transactionService: TransactionWritesServiceGrpcKt.TransactionWritesServiceCoroutineStub) : TransactionImportServiceGrpcKt.TransactionImportServiceCoroutineImplBase() {

  private val logger = LoggerFactory.getLogger(javaClass)

  override suspend fun importTransactions(request: ImportTransactionsRequest): ImportTransactionsResponse {
    logger.info("Importing transactions: ${request.transactions}")
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
            OpenBankAccountRequest
              .newBuilder()
              .setAccountNumber(if (transaction.direction == "Af") transaction.firstAccount else transaction.secondAccount)
              .setInitialBalance(5000F) // FIXME: Make configurable
              .build()
          )
        }

        if (transaction.direction == "Af") {
          transactionService.withdraw(
            WithdrawRequest
              .newBuilder()
              .setAmountInCents((transaction.amount.replace(',', '.').toFloat() * 100).toLong())
              .setDate(transaction.date)
              .setDetails(transaction.details)
              .setName(transaction.name)
              .setFromAccount(transaction.firstAccount)
              .setToAccount(transaction.secondAccount)
              .setType(transaction.type)
              .build()
          )
        } else {
          transactionService.deposit(
            DepositRequest
              .newBuilder()
              .setAmountInCents((transaction.amount.replace(',', '.').toFloat() * 100).toLong())
              .setDate(transaction.date)
              .setDetails(transaction.details)
              .setName(transaction.name)
              .setFromAccount(transaction.secondAccount)
              .setToAccount(transaction.firstAccount)
              .setType(transaction.type)
              .build()
          )
        }

      }
    }

    return ImportTransactionsResponse.newBuilder().build()
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
