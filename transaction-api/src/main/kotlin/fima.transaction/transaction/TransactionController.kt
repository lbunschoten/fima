package fima.transaction.transaction

import fima.services.transaction.GetTransactionRequest
import fima.services.transaction.TransactionServiceGrpc
import fima.services.transactionstatistics.TransactionStatisticsServiceGrpc
import fima.services.transactionstatistics.TransactionsStatisticsRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class TransactionController @Autowired constructor(
        private val transactionService: TransactionServiceGrpc.TransactionServiceBlockingStub,
        private val transactionStatisticsService: TransactionStatisticsServiceGrpc.TransactionStatisticsServiceBlockingStub
) {

    @GetMapping("/{id}")
    fun getTransaction(@PathVariable("id") transactionId: Int): fima.transaction.transaction.Transaction {
        val request = GetTransactionRequest.newBuilder().setId(transactionId).build()

        return transactionService.getTransaction(request).transaction.simple()
    }

    @GetMapping("/statistics")
    fun getStatistics(): TransactionStatistics {
        return transactionStatisticsService.getStatistics(TransactionsStatisticsRequest.newBuilder().build()).simple()
    }

}