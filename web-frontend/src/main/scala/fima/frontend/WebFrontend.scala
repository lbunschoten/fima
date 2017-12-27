package fima.frontend

import java.net.InetSocketAddress

import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.thrift.RichClientParam
import com.twitter.finagle.{Thrift, thrift}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import fima.transactionservice.thriftscala.TransactionService$FinagleClient

object WebFrontend extends HttpServer {

  override protected def disableAdminHttpServer = true
  override val defaultFinatraHttpPort = ":8890"

  def transactionService: TransactionService$FinagleClient = new TransactionService$FinagleClient(thriftClientBuilder("localhost", 9997), RichClientParam(
    protocolFactory = thrift.Protocols.binaryFactory(),
    "transactionService"
  ))

  private def thriftClientBuilder(host: String, port: Int) = ClientBuilder()
    .hosts(Seq(new InetSocketAddress(host, port)))
    .stack(Thrift.client)
    .hostConnectionLimit(1)
    .failFast(false)
    .build()

  override protected def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add(new IndexController(transactionService))
  }

}
