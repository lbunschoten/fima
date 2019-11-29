package fima.services.transaction.read

import fima.domain.transaction.Date
import fima.domain.transaction.Transaction
import fima.domain.transaction.TransactionType
import fima.services.transaction.*
import fima.services.transaction.read.store.TransactionStatisticsReadsStore
import fima.services.transaction.read.store.TransactionsReadsStore
import io.grpc.stub.StreamObserver

class TransactionReadsServiceImpl(private val transactionsStore: TransactionsReadsStore,
                                  private val transactionStatisticsStore: TransactionStatisticsReadsStore) : TransactionServiceGrpc.TransactionServiceImplBase() {

  override fun getTransaction(request: GetTransactionRequest, responseObserver: StreamObserver<GetTransactionResponse>) {
    val response = GetTransactionResponse.newBuilder()
      .setTransaction(
        transactionsStore.getById(request.id)?.toProto() ?: run {
          Transaction.newBuilder()
            .setId(1)
            .setName("test transaction")
            .setDescription("test transaction description")
            .setDate(Date.newBuilder().setDay(1).setMonth(2).setYear(2018).build())
            .setAmount(20.32F)
            .setFromAccount("From account")
            .setToAccount("From account")
            .setType(TransactionType.TRANSFER)
            .build()
        }
      )
      .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }

  override fun getRecentTransactions(request: GetRecentTransactionsRequest, responseObserver: StreamObserver<GetRecentTransactionResponse>) {
    val response = GetRecentTransactionResponse.newBuilder()
      .addAllTransactions(transactionsStore.getRecent(request.offset, request.limit).map { it.toProto() })
      .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }

  override fun getMonthlyStatistics(request: TransactionsStatisticsRequest, responseObserver: StreamObserver<TransactionStatisticsResponse>) {
    val responseBuilder = TransactionStatisticsResponse
      .newBuilder()
      .addAllMonthlyStatistics(
        transactionStatisticsStore
          .getMonthlyStatistics(request.startDate.month, request.startDate.year, request.endDate.month, request.endDate.year)
          .map {
            MonthlyStatistics
              .newBuilder()
              .setMonth(it.month)
              .setYear(it.year)
              .setTransaction(it.numTransactions)
              .setSum(it.sum.toFloat())
              .setBalance(it.balance.toFloat())
              .build()
          }
      )

    responseObserver.onNext(responseBuilder.build())
    responseObserver.onCompleted()
  }

}