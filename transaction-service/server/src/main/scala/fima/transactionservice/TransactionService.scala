package fima.transactionservice

import com.twitter.finatra.thrift.Controller
import com.twitter.util.Future
import fima.transactionservice.thriftscala.Transaction
import fima.transactionservice.thriftscala.TransactionService.{DeleteTransaction, GetTransaction, InsertTransaction, ServicePerEndpoint}

class TransactionService extends Controller with ServicePerEndpoint {

  override val insertTransaction = handle(InsertTransaction) { args: InsertTransaction.Args =>
    ???
  }

  override val getTransaction = handle(GetTransaction) { args: GetTransaction.Args =>
    Future(Transaction(1))
  }

  override val deleteTransaction = handle(DeleteTransaction) { args: DeleteTransaction.Args =>
    ???
  }

}
