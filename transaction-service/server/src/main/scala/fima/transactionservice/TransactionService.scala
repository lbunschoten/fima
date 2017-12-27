package fima.transactionservice

import com.twitter.finatra.thrift.Controller
import fima.transactionservice.thriftscala.TransactionService.{BaseServiceIface, DeleteTransaction, GetTransaction, InsertTransaction}

class TransactionService extends Controller with BaseServiceIface {

  override val insertTransaction = handle(InsertTransaction) { args: InsertTransaction.Args =>
    ???
  }

  override val getTransaction = handle(GetTransaction) { args: GetTransaction.Args =>
    ???
  }

  override val deleteTransaction = handle(DeleteTransaction) { args: DeleteTransaction.Args =>
    ???
  }

}
