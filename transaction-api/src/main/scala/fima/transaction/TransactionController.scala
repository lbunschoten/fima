package fima.transaction

import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.QueryParam
import fima.services.transaction.TransactionServiceGrpc.TransactionServiceBlockingStub
import fima.services.transaction.{GetRecentTransactionsRequest => PGetRecentTransactionsRequest, GetTransactionRequest => PGetTransactionRequest, InsertTransactionRequest => PInsertTransactionRequest, DeleteTransactionRequest => PDeleteTransactionRequest}
import fima.services.transactionstatistics.TransactionStatisticsServiceGrpc.TransactionStatisticsServiceBlockingStub
import fima.services.transactionstatistics.{TransactionStatisticsResponse => PTransactionStatisticsResponse, TransactionsStatisticsRequest => PTransactionsStatisticsRequest}
import fima.transaction.model.Transaction

class TransactionController(transactionService: TransactionServiceBlockingStub,
                            transactionStatisticsService: TransactionStatisticsServiceBlockingStub) extends Controller {

  get("/api/transaction/:id") { _: GetTransactionRequest =>
    val request = PGetTransactionRequest.newBuilder().setId(1).build()
    val transaction = transactionService.getTransaction(request).getTransaction

    Transaction.fromProto(transaction)
  }

  get("/api/transaction/recent") { _: GetRecentTransactionRequest =>
    val request = PGetRecentTransactionsRequest.newBuilder().setLimit(5).build()
    transactionService.getRecentTransactions(request)
  }

  get("/api/transaction/statistics") { _: GetTransactionStatisticsRequest =>
    val a: PTransactionStatisticsResponse = transactionStatisticsService.getStatistics(PTransactionsStatisticsRequest.newBuilder().setName("abc").build())
    StatisticsResponse(a.getTransactions)
  }

  put("/api/transaction") { _: PutTransactionRequest =>
    val request = PInsertTransactionRequest.newBuilder().build()
    transactionService.insertTransaction(request)
  }

  delete("/api/transaction/:id") { r: DeleteTransactionRequest =>
    val request: PDeleteTransactionRequest = PDeleteTransactionRequest.newBuilder().setId(r.id).build()
    transactionService.deleteTransaction(request)
  }

}

case class GetTransactionRequest(@QueryParam id: Int)

case class GetTransactionStatisticsRequest()

case class GetRecentTransactionRequest(@QueryParam limit: Int)

case class PutTransactionRequest(@QueryParam description: String)

case class DeleteTransactionRequest(@QueryParam id: Int)

case class StatisticsResponse(transactions: Int)