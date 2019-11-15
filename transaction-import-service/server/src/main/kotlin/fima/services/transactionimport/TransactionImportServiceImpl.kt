package fima.services.transactionimport

import com.opencsv.bean.CsvBindByPosition
import com.opencsv.bean.CsvToBeanBuilder
import fima.services.transaction.write.*
import io.grpc.stub.StreamObserver
import java.io.StringReader

class TransactionImportServiceImpl(private val transactionService: TransactionWritesServiceGrpc.TransactionWritesServiceBlockingStub) : TransactionImportServiceGrpc.TransactionImportServiceImplBase() {

  override fun importTransactions(request: ImportTransactionsRequest, responseObserver: StreamObserver<ImportTransactionsResponse>) {
    StringReader(request.transactions).use { reader ->
      val csvReader = CsvToBeanBuilder<Transaction>(reader)
        .withSeparator(',')
        .withQuoteChar('\"')
        .withType(Transaction::class.java)
        .withThrowExceptions(true)
        .withSkipLines(1)
        .build()

      csvReader.parse().forEachIndexed { index, transaction ->
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

    responseObserver.onNext(ImportTransactionsResponse.newBuilder().build())
    responseObserver.onCompleted()
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
