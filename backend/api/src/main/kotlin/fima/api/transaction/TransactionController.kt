package fima.api.transaction

import fima.domain.transaction.MonthInYear
import fima.services.transaction.GetRecentTransactionsRequest
import fima.services.transaction.GetTransactionRequest
import fima.services.transaction.TransactionServiceGrpcKt
import fima.services.transaction.TransactionsStatisticsRequest
import fima.services.transactionimport.ImportTransactionsRequest
import fima.services.transactionimport.TransactionImportServiceGrpcKt
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.nio.charset.Charset
import java.util.UUID

@RestController
class TransactionController @Autowired constructor(
    private val transactionService: TransactionServiceGrpcKt.TransactionServiceCoroutineStub,
    private val transactionImportService: TransactionImportServiceGrpcKt.TransactionImportServiceCoroutineStub
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @CrossOrigin
    @GetMapping("/transaction/{id}")
    suspend fun getTransaction(@PathVariable("id") transactionId: UUID): Transaction {
        val request = GetTransactionRequest.newBuilder().setId(transactionId.toString()).build()

        return transactionService.getTransaction(request).transaction.simple()
    }

    @CrossOrigin
    @GetMapping("/transaction/recent")
    suspend fun getRecentTransactions(@RequestParam("offset") offset: Int, @RequestParam("limit") limit: Int): List<Transaction> {
        val request = GetRecentTransactionsRequest
            .newBuilder()
            .setOffset(offset)
            .setLimit(limit)
            .build()

        return transactionService.getRecentTransactions(request).transactionsList.map { it.simple() }
    }

    @CrossOrigin
    @GetMapping("/transaction/statistics")
    suspend fun getStatistics(): List<MonthlyTransactionStatistics> {
        val startOfYear = MonthInYear.newBuilder().setMonth(1).setYear(2020)
        val endOfYear = MonthInYear.newBuilder().setMonth(12).setYear(2020)
        return transactionService
            .getMonthlyStatistics(TransactionsStatisticsRequest.newBuilder().setStartDate(startOfYear).setEndDate(endOfYear).build())
            .monthlyStatisticsList
            .map { it.simple() }
    }

    @PutMapping("/transaction/import")
    suspend fun importTransactions(@RequestPart("transactions", required = true) transactions: Mono<FilePart>): ResponseEntity<String> {
        logger.info("Received import request")

        return transactions
            .flatMap {
                DataBufferUtils.join(it.content()).map { dataBuffer ->
                    dataBuffer.asInputStream().use { input ->
                        val request = ImportTransactionsRequest
                            .newBuilder()
                            .setTransactions(String(input.readAllBytes(), Charset.forName("UTF-8")))
                            .build()

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