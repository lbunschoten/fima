package fima.transaction.transaction

import fima.services.transaction.GetRecentTransactionsRequest
import fima.services.transaction.GetTransactionRequest
import fima.services.transaction.MonthInYear
import fima.services.transaction.TransactionServiceGrpcKt
import fima.services.transaction.TransactionsStatisticsRequest
import fima.services.transactionimport.ImportTransactionsRequest
import fima.services.transactionimport.TransactionImportServiceGrpcKt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.Charset
import java.util.UUID

@RestController
class TransactionController @Autowired constructor(
  private val transactionService: TransactionServiceGrpcKt.TransactionServiceCoroutineStub,
  private val transactionImportService: TransactionImportServiceGrpcKt.TransactionImportServiceCoroutineStub
) {

  @GetMapping("/{id}")
  suspend fun getTransaction(@PathVariable("id") transactionId: UUID): Transaction {
    val request = GetTransactionRequest.newBuilder().setId(transactionId.toString()).build()

    return transactionService.getTransaction(request).transaction.simple()
  }

  @GetMapping("/recent")
  suspend fun getRecentTransactions(@RequestParam("offset") offset: Int, @RequestParam("limit") limit: Int): List<Transaction> {
    val request = GetRecentTransactionsRequest
      .newBuilder()
      .setOffset(offset)
      .setLimit(limit)
      .build()

    return transactionService.getRecentTransactions(request).transactionsList.map { it.simple() }
  }

  @GetMapping("/statistics")
  suspend fun getStatistics(): List<TransactionStatistics> {
    val startOfYear = MonthInYear.newBuilder().setMonth(1).setYear(2018)
    val endOfYear = MonthInYear.newBuilder().setMonth(12).setYear(2018)
    return transactionService
      .getMonthlyStatistics(TransactionsStatisticsRequest.newBuilder().setStartDate(startOfYear).setEndDate(endOfYear).build())
      .monthlyStatisticsList
      .map { it.simple() }
  }

  @PutMapping("/import")
  suspend fun importTransactions(@RequestParam("transactions") transactions: MultipartFile) {
    val request = ImportTransactionsRequest.newBuilder().setTransactions(String(transactions.bytes, Charset.forName("UTF-8"))).build()
    transactionImportService.importTransactions(request)
  }

}