package fima.transaction

import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.QueryParam
import fima.transactionservice.thriftscala.{Transaction, TransactionService$FinagleClient}

class TransactionController(transactionService: TransactionService$FinagleClient) extends Controller {

  get("/api/transaction/:id") { request: GetTransactionRequest =>
    transactionService.getTransaction(request.id)
  }

  put("/api/transaction") { request: PutTransactionRequest =>
    transactionService.insertTransaction(Transaction(0))
  }

  delete("/api/transaction/:id") { request: DeleteTransactionRequest =>
    transactionService.deleteTransaction(request.id)
  }

}

case class GetTransactionRequest(@QueryParam id: Int)

case class PutTransactionRequest(@QueryParam description: String)

case class DeleteTransactionRequest(@QueryParam id: Int)