package fima.frontend

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class IndexController extends Controller {

  get("/index.html") { _: Request =>
    response.ok.file("index.html")
  }

  get("/:*") { request: Request =>
    response.ok.file(request.params("*"))
  }

  case class Transaction(label: String)

}

