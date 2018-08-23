package fima.services.transactionstatistics

import fima.services.transactionstatistics.repository.StatisticsRepository
import io.grpc.stub.ServerCallStreamObserver
import io.grpc.stub.ServerCalls
import io.grpc.stub.StreamObserver
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify

class TransactionStatisticsServiceImplSpec : StringSpec() {

  override fun isInstancePerTest(): Boolean = true

  init {

    val statisticsRepository = mockk<StatisticsRepository>()
    val service = TransactionStatisticsServiceImpl(statisticsRepository)
    val request = TransactionsStatisticsRequest.getDefaultInstance()
    val responseSpy = spyk<NoOpStreamObserver<TransactionStatisticsResponse>>(NoOpStreamObserver())

    "it should return the statistics for a given month and year" {
      // FIXME (doesn't take month or year)
      every { statisticsRepository.getStatistics(any(), any()) } returns StatisticsRepository.MonthlyTransactionStatistics(1, 2018, 12)

      service.getStatistics(request, responseSpy)

      verify { responseSpy.onNext(transactionStatisticsResponse(1, 2018, 12)) }
    }

    "it should return 0 statistics when no transactions are available" {
      // FIXME (doesn't take month or year)
      every { statisticsRepository.getStatistics(any(), any()) } returns null

      service.getStatistics(request, responseSpy)

      verify { responseSpy.onNext(transactionStatisticsResponse(0, 0, 0)) }
    }

  }

  private fun transactionStatisticsResponse(month: Int, year: Int, transactions: Int): TransactionStatisticsResponse {
    return TransactionStatisticsResponse
      .newBuilder()
      .setMonth(month)
      .setYear(year)
      .setTransactions(transactions)
      .build()
  }

  inner class NoOpStreamObserver<T> : StreamObserver<T> {
    override fun onCompleted() {
    }

    override fun onError(p0: Throwable?) {
    }

    override fun onNext(p0: T) {}
  }

}