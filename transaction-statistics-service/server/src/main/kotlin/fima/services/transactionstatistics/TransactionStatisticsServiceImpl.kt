package fima.services.transactionstatistics

import fima.services.transactionstatistics.repository.StatisticsRepository
import io.grpc.stub.StreamObserver

class TransactionStatisticsServiceImpl(private val statisticsRepository: StatisticsRepository) : TransactionStatisticsServiceGrpc.TransactionStatisticsServiceImplBase() {

  override fun getMonthlyStatistics(request: TransactionsStatisticsRequest, responseObserver: StreamObserver<TransactionStatisticsResponse>) {
    val responseBuilder = TransactionStatisticsResponse
      .newBuilder()
      .addAllMonthlyStatistics(statisticsRepository
        .getMonthlyStatistics(request.startDate.month, request.startDate.year, request.endDate.month, request.endDate.year)
        .map {
          MonthlyStatistics
            .newBuilder()
            .setMonth(it.month)
            .setYear(it.year)
            .setTransaction(it.numTransactions)
            .build()
        }
      )

    responseObserver.onNext(responseBuilder.build())
    responseObserver.onCompleted()
  }

}