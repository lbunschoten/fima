package fima.transaction.transaction

import fima.services.transaction.GetRecentTransactionsRequest
import fima.services.transaction.GetTransactionRequest
import fima.services.transaction.TransactionServiceGrpc
import fima.services.transactionimport.ImportTransactionsRequest
import fima.services.transactionimport.TransactionImportServiceGrpc
import fima.services.transactionstatistics.MonthInYear
import fima.services.transactionstatistics.TransactionStatisticsServiceGrpc
import fima.services.transactionstatistics.TransactionsStatisticsRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.Charset

@RestController
class TransactionController @Autowired constructor(
  private val transactionService: TransactionServiceGrpc.TransactionServiceBlockingStub,
  private val transactionStatisticsService: TransactionStatisticsServiceGrpc.TransactionStatisticsServiceBlockingStub,
  private val transactionImportService: TransactionImportServiceGrpc.TransactionImportServiceBlockingStub
) {

  @CrossOrigin
  @GetMapping("/{id}")
  fun getTransaction(@PathVariable("id") transactionId: Int): Transaction {
    val request = GetTransactionRequest.newBuilder().setId(transactionId).build()

    return transactionService.getTransaction(request).transaction.simple()
  }

  @CrossOrigin
  @GetMapping("/recent")
  fun getRecentTransactions(@RequestParam("offset") offset: Int, @RequestParam("limit") limit: Int): List<Transaction> {
    val request = GetRecentTransactionsRequest
      .newBuilder()
      .setOffset(offset)
      .setLimit(limit)
      .build()

    return transactionService.getRecentTransactions(request).transactionsList.map { it.simple() }
  }

  @CrossOrigin
  @GetMapping("/statistics")
  fun getStatistics(): List<TransactionStatistics> {
    val startOfYear = MonthInYear.newBuilder().setMonth(1).setYear(2018)
    val endOfYear = MonthInYear.newBuilder().setMonth(12).setYear(2018)
    return transactionStatisticsService
      .getMonthlyStatistics(TransactionsStatisticsRequest.newBuilder().setStartDate(startOfYear).setEndDate(endOfYear).build())
      .monthlyStatisticsList
      .map { it.simple() }
  }

  @PutMapping("/import")
  fun importTransactions(@RequestParam("transactions") transactions: MultipartFile) {
    val request = ImportTransactionsRequest.newBuilder().setTransactions(String(transactions.bytes, Charset.forName("UTF-8"))).build()
    transactionImportService.importTransactions(request)
  }

}