package fima.transaction.transaction

import fima.services.transaction.GetRecentTransactionsRequest
import fima.services.transaction.GetTransactionRequest
import fima.services.transaction.MonthInYear
import fima.services.transaction.TransactionServiceGrpcKt
import fima.services.transaction.TransactionsStatisticsRequest
import fima.services.transactionimport.TransactionImportServiceGrpcKt
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.nio.charset.StandardCharsets
import java.util.UUID

@RestController
class TransactionController @Autowired constructor(
  private val transactionService: TransactionServiceGrpcKt.TransactionServiceCoroutineStub,
  private val transactionImportService: TransactionImportServiceGrpcKt.TransactionImportServiceCoroutineStub
) {

  private val logger = LoggerFactory.getLogger(javaClass)

  @CrossOrigin
  @GetMapping("/{id}")
  suspend fun getTransaction(@PathVariable("id") transactionId: UUID): Transaction {
    val request = GetTransactionRequest.newBuilder().setId(transactionId.toString()).build()

    return transactionService.getTransaction(request).transaction.simple()
  }

  @CrossOrigin
  @GetMapping("/recent")
  suspend fun getRecentTransactions(@RequestParam("offset") offset: Int, @RequestParam("limit") limit: Int): List<Transaction> {
    val request = GetRecentTransactionsRequest
      .newBuilder()
      .setOffset(offset)
      .setLimit(limit)
      .build()

    return transactionService.getRecentTransactions(request).transactionsList.map { it.simple() }
  }

  @CrossOrigin
  @GetMapping("/statistics")
  suspend fun getStatistics(): List<TransactionStatistics> {
    val startOfYear = MonthInYear.newBuilder().setMonth(1).setYear(2018)
    val endOfYear = MonthInYear.newBuilder().setMonth(12).setYear(2018)
    return transactionService
      .getMonthlyStatistics(TransactionsStatisticsRequest.newBuilder().setStartDate(startOfYear).setEndDate(endOfYear).build())
      .monthlyStatisticsList
      .map { it.simple() }
  }

  @PutMapping("/import", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
  suspend fun importTransactions(@RequestBody transactions: FilePart): Flux<String> {
      return transactions.content().map { dataBuffer ->
          val bytes = ByteArray(dataBuffer.readableByteCount())
          dataBuffer.read(bytes)
          DataBufferUtils.release(dataBuffer)

          String(bytes, StandardCharsets.UTF_8)
      }
      .map(this::processAndGetLinesAsList)
      .flatMapIterable { it }
  }

  private fun processAndGetLinesAsList(s: String): List<String> {
    return s.lines().map {
      logger.info(it)
      it
    }
  }
}