package fima.api.transaction

import fima.domain.transaction.monthInYear
import fima.services.transaction.*
import fima.services.transactionimport.TransactionImportServiceGrpcKt
import fima.services.transactionimport.importTransactionsRequest
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.nio.charset.Charset
import java.util.*

@RestController
class TransactionController @Autowired constructor(
    private val transactionService: TransactionServiceGrpcKt.TransactionServiceCoroutineStub,
    private val transactionImportService: TransactionImportServiceGrpcKt.TransactionImportServiceCoroutineStub
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @CrossOrigin
    @GetMapping("/transaction/{id}")
    suspend fun getTransaction(@PathVariable("id") transactionId: UUID): Transaction {
        val request = getTransactionRequest { id = transactionId.toString() }

        return transactionService
            .getTransaction(request)
            .transaction
            .let(Transaction::fromProto)
    }

    @CrossOrigin
    @GetMapping("/transaction/recent")
    suspend fun getRecentTransactions(@RequestParam("offset") offset: Int, @RequestParam("limit") limit: Int): List<Transaction> {
        logger.info("Received request for recent transactions")

        val request = getRecentTransactionsRequest { this.offset = offset; this.limit = limit }

        return transactionService
            .getRecentTransactions(request)
            .transactionsList
            .map(Transaction::fromProto)
    }

    @CrossOrigin
    @GetMapping("/transaction/statistics")
    suspend fun getStatistics(): List<MonthlyTransactionStatistics> {
        logger.info("Received request for transaction statistics")

        val startOfYear = monthInYear { month = 1; year = 2020 }
        val endOfYear = monthInYear { month = 12; year = 2020 }
        return transactionService
            .getMonthlyStatistics(transactionsStatisticsRequest {
                startDate = startOfYear
                endDate = endOfYear
            })
            .monthlyStatisticsList
            .map(MonthlyTransactionStatistics::fromProto)
    }

    @GetMapping("/tag")
    suspend fun tagTransactions(): ResponseEntity<String> {
        logger.info("Received request for tagging all transactions")

        transactionService.tagTransactions(tagTransactionsRequest { })

        return ResponseEntity.ok("Successfully tagged all transactions")
    }

    @PutMapping("/transaction/import")
    suspend fun importTransactions(@RequestPart("transactions", required = true) transactions: Mono<FilePart>): ResponseEntity<String> {
        logger.info("Received import request")

        return transactions
            .flatMap {
                DataBufferUtils.join(it.content()).map { dataBuffer ->
                    dataBuffer.asInputStream().use { input ->
                        val request = importTransactionsRequest {
                            this.transactions = String(input.readAllBytes(), Charset.forName("UTF-8"))
                        }

                        suspend {
                            transactionImportService.importTransactions(request)
                        }
                    }
                }
            }.map {
                ResponseEntity.ok("Upload successful")
            }.awaitSingle()
    }
}