package fima.frontend

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import fima.transactionservice.thriftscala.TransactionService$FinagleClient

class IndexController(transactionService: TransactionService$FinagleClient) extends Controller {

  get("/") { _: Request =>
    val a = transactionService.getTransaction(1)
    a.map { t =>
      response.ok.view("index.mustache", Map(
        "transactions" -> Seq(Transaction(t.id.toString), Transaction("b"))
      ))
    }
  }

  get("/assets/:*") { request: Request =>
    response.ok.file(request.params("*"))
  }

  case class Transaction(label: String)

}

