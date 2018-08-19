package fima.services.transactionimport

import com.opencsv.bean.CsvBindByPosition
import com.opencsv.bean.CsvToBeanBuilder
import fima.domain.transaction.RawTransaction
import fima.services.transaction.InsertTransactionRequest
import fima.services.transaction.TransactionServiceGrpc
import io.grpc.stub.StreamObserver
import java.io.StringReader

class TransactionImportServiceImpl(private val transactionService: TransactionServiceGrpc.TransactionServiceBlockingStub) : TransactionImportServiceGrpc.TransactionImportServiceImplBase() {

    override fun importTransactions(request: ImportTransactionsRequest, responseObserver: StreamObserver<ImportTransactionsResponse>) {
        StringReader(request.transactions).use { reader ->
            val csvReader = CsvToBeanBuilder<Transaction>(reader)
                    .withSeparator(',')
                    .withQuoteChar('\"')
                    .withType(Transaction::class.java)
                    .withThrowExceptions(true)
                    .withSkipLines(1)
                    .build()

            csvReader.parse().forEach {
                val transaction = RawTransaction.newBuilder().run {
                    date = it.date
                    name = it.name
                    type = it.type
                    amount = it.amount.replace(',', '.').toFloat()
                    details = it.details

                    if (it.direction == "Af") {
                        fromAccount = it.firstAccount
                        toAccount = it.secondAccount
                    } else {
                        fromAccount = it.secondAccount
                        toAccount = it.firstAccount
                    }

                    build()
                }

                transactionService.insertTransaction(
                        InsertTransactionRequest
                                .newBuilder()
                                .setTransaction(transaction)
                                .build()
                )
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
