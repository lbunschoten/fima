package fima.services.transaction

import fima.services.transaction.store.TransactionsStore
import fima.services.transaction.write.TransactionService
import io.grpc.Status
import io.grpc.StatusException
import org.slf4j.LoggerFactory

class GrpcTransactionService(
    private val transactionService: TransactionService
) : TransactionServiceGrpcKt.TransactionServiceCoroutineImplBase() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun searchTransactions(request: SearchTransactionsRequest): SearchTransactionsResponse {
        try {
            logger.info("Received search request for transactions: $request")
            val transactions = transactionService.searchTransactions(
                request.filtersList.map { f ->
                    TransactionsStore.SearchFilters(
                        queryFilter = f?.query?.queryString?.takeIf { it.isNotBlank() },
                        tagFilters = f.tagsList.associate { filter -> filter.key to filter.value }
                    )
                }
            )

            logger.info("Found ${transactions.size} after search request")

            return searchTransactionsResponse {
                this.transactions.addAll(transactions.map { it.toProto() })
            }
        } catch (e: Exception) {
            logger.error("Failed to search for transactions: ${e.message}")
            throw StatusException(Status.UNKNOWN.withCause(e))
        }
    }
}