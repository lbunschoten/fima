package fima.transaction

import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.QueryParam
import fima.services.transaction
import fima.services.transaction.TransactionServiceGrpc.TransactionServiceBlockingStub
import fima.services.transactionstatistics.TransactionStatisticsServiceGrpc.TransactionStatisticsServiceBlockingStub
import fima.services.transactionstatistics.{TransactionStatisticsResponse, TransactionsStatisticsRequest}

class TransactionController(transactionService: TransactionServiceBlockingStub,
                            transactionStatisticsService: TransactionStatisticsServiceBlockingStub) extends Controller {

  get("/api/transaction/:id") { request: GetTransactionRequest =>
    val request = transaction.GetTransactionRequest.newBuilder().setId(1).build()
    val r = transactionService.getTransaction(request)

    println(s"DEBUG: ${r.getTransaction.getId}")

    r
  }

  get("/api/transaction/recent") { request: RecentTransactionRequest =>
    val request = transaction.GetRecentTransactionsRequest.newBuilder().setLimit(5).build()
    transactionService.getRecentTransactions(request)
  }

  get("/api/transaction/statistics") { request: TransactionStatisticsRequest =>
    val a: TransactionStatisticsResponse = transactionStatisticsService.getStatistics(TransactionsStatisticsRequest.newBuilder().setName("abc").build())
    StatisticsResponse(a.getTransactions)
  }

  put("/api/transaction") { request: PutTransactionRequest =>
    val request = transaction.InsertTransactionRequest.newBuilder().build()
    transactionService.insertTransaction(request)
  }

  delete("/api/transaction/:id") { request: DeleteTransactionRequest =>
    val request = transaction.DeleteTransactionRequest.newBuilder().build()
    transactionService.deleteTransaction(request)
  }

}

case class GetTransactionRequest(@QueryParam id: Int)

case class TransactionStatisticsRequest()

case class RecentTransactionRequest(@QueryParam limit: Int)

case class PutTransactionRequest(@QueryParam description: String)

case class DeleteTransactionRequest(@QueryParam id: Int)

case class StatisticsResponse(transactions: Int)