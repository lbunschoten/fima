package fima.transaction.transaction

import fima.services.transaction.GetRecentTransactionsRequest
import fima.services.transaction.GetTransactionRequest
import fima.services.transaction.TransactionServiceGrpc
import fima.services.transactionstatistics.TransactionStatisticsServiceGrpc
import fima.services.transactionstatistics.TransactionsStatisticsRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TransactionController @Autowired constructor(
        private val transactionService: TransactionServiceGrpc.TransactionServiceBlockingStub,
        private val transactionStatisticsService: TransactionStatisticsServiceGrpc.TransactionStatisticsServiceBlockingStub
) {

    @GetMapping("/{id}")
    fun getTransaction(@PathVariable("id") transactionId: Int): Transaction {
        val request = GetTransactionRequest.newBuilder().setId(transactionId).build()

        return transactionService.getTransaction(request).transaction.simple()
    }

    @GetMapping("/recent")
    fun getRecentTransactions(@RequestParam("limit") limit: Int): List<Transaction> {
        val request = GetRecentTransactionsRequest.newBuilder().setLimit(limit).build()

        return transactionService.getRecentTransactions(request).transactionsList.map { it.simple() }
    }

    @GetMapping("/statistics")
    fun getStatistics(): TransactionStatistics {
        return transactionStatisticsService.getStatistics(TransactionsStatisticsRequest.newBuilder().build()).simple()
    }

}