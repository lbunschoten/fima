package fima.services.transactionstatistics

import fima.services.transactionstatistics.repository.StatisticsRepository
import io.grpc.stub.StreamObserver

class TransactionStatisticsServiceImpl(private val statisticsRepository: StatisticsRepository) : TransactionStatisticsServiceGrpc.TransactionStatisticsServiceImplBase() {

  override fun getMonthlyStatistics(request: TransactionsStatisticsRequest, responseObserver: StreamObserver<TransactionStatisticsResponse>) {
    val responseBuilder = TransactionStatisticsResponse
      .newBuilder()

    statisticsRepository
      .getMonthlyStatistics(request.startDate.month, request.startDate.year, request.endDate.month, request.endDate.year)
      .mapIndexed { index, monthlyTransactionStatistics ->
        val s = MonthlyStatistics
          .newBuilder()
          .setMonth(monthlyTransactionStatistics.month)
          .setYear(monthlyTransactionStatistics.year)
          .setTransaction(monthlyTransactionStatistics.numTransactions)
          .build()

        responseBuilder.putMonthlyStatistics(index, s)
      }

    responseObserver.onNext(responseBuilder.build())
    responseObserver.onCompleted()
  }

}