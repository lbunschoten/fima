package fima.services.transaction.read

import fima.services.transaction.GetRecentTransactionResponse
import fima.services.transaction.GetRecentTransactionsRequest
import fima.services.transaction.GetTransactionRequest
import fima.services.transaction.GetTransactionResponse
import fima.services.transaction.TransactionServiceGrpcKt
import fima.services.transaction.TransactionStatisticsResponse
import fima.services.transaction.TransactionsStatisticsRequest
import fima.services.transaction.read.store.TransactionReads
import fima.services.transaction.read.store.TransactionStatisticsStore
import io.grpc.Status
import io.grpc.StatusException
import org.slf4j.LoggerFactory

class TransactionReadsServiceImpl(
    private val transactionsStore: TransactionReads,
    private val transactionStatisticsStore: TransactionStatisticsStore
) : TransactionServiceGrpcKt.TransactionServiceCoroutineImplBase() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun getTransaction(request: GetTransactionRequest): GetTransactionResponse {
        return GetTransactionResponse
            .newBuilder()
            .setTransaction(
                transactionsStore
                    .getById(request.id)
                    .toProto()
            )
            .build()
    }

    override suspend fun getRecentTransactions(request: GetRecentTransactionsRequest): GetRecentTransactionResponse {
        return try {
            GetRecentTransactionResponse
                .newBuilder()
                .addAllTransactions(
                    transactionsStore
                        .getRecent(request.offset, request.limit)
                        .map { it.toProto() }
                )
                .build()
        } catch (e: Exception) {
            logger.error("Failed retrieving getting recent transactions: ${e.message}")
            throw StatusException(Status.UNKNOWN.withCause(e))
        }
    }

    override suspend fun getMonthlyStatistics(request: TransactionsStatisticsRequest): TransactionStatisticsResponse {
        return TransactionStatisticsResponse
            .newBuilder()
            .addAllMonthlyStatistics(
                transactionStatisticsStore
                    .getMonthlyStatistics(request.startDate.month, request.startDate.year, request.endDate.month, request.endDate.year)
                    .map { it.toProto() }
            )
            .build()
    }

}