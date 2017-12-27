package fima.frontend

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class IndexController extends Controller {

  get("/") { _: Request =>
    response.ok.view("index.mustache", Map(
      "transactions" -> Seq(Transaction("a"), Transaction("b"))
    ))
  }

  get("/assets/:*") { request: Request =>
    response.ok.file(request.params("*"))
  }

  case class Transaction(label: String)

}

