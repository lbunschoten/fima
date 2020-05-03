package fima.services.transaction.read

import fima.services.transaction.GetRecentTransactionResponse
import fima.services.transaction.GetRecentTransactionsRequest
import fima.services.transaction.GetTransactionRequest
import fima.services.transaction.GetTransactionResponse
import fima.services.transaction.MonthlyStatistics
import fima.services.transaction.TransactionServiceGrpc
import fima.services.transaction.TransactionStatisticsResponse
import fima.services.transaction.TransactionsStatisticsRequest
import fima.services.transaction.read.store.TransactionReads
import fima.services.transaction.read.store.TransactionStatisticsReadsStore
import io.grpc.stub.StreamObserver

class TransactionReadsServiceImpl(private val transactionsStore: TransactionReads,
                                  private val transactionStatisticsStore: TransactionStatisticsReadsStore) : TransactionServiceGrpc.TransactionServiceImplBase() {

  override fun getTransaction(request: GetTransactionRequest, responseObserver: StreamObserver<GetTransactionResponse>) {
    val response = GetTransactionResponse.newBuilder()
      .setTransaction(transactionsStore.getById(request.id))
      .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }

  override fun getRecentTransactions(request: GetRecentTransactionsRequest, responseObserver: StreamObserver<GetRecentTransactionResponse>) {
    val response = GetRecentTransactionResponse.newBuilder()
      .addAllTransactions(transactionsStore.getRecent(request.offset, request.limit))
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