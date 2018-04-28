package fima.services.transactionstatistics

import fima.services.transactionstatistics.repository.StatisticsRepository
import io.grpc.stub.StreamObserver
import java.time.LocalDate

class TransactionStatisticsServiceImpl(private val statisticsRepository: StatisticsRepository) : TransactionStatisticsServiceGrpc.TransactionStatisticsServiceImplBase() {

    override fun getStatistics(request: TransactionsStatisticsRequest, responseObserver: StreamObserver<TransactionStatisticsResponse>) {
        val now = LocalDate.now()
        val statistics = statisticsRepository.getStatistics(now.monthValue, now.year)

        val response = TransactionStatisticsResponse
                .newBuilder()
                .setMonth(statistics?.month ?: 0)
                .setYear(statistics?.year ?: 0)
                .setTransactions(statistics?.numTransactions ?: 0)
                .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

}