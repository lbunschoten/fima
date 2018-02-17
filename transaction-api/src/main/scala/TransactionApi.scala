import java.net.InetSocketAddress

import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.thrift.RichClientParam
import com.twitter.finagle.{Thrift, thrift}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import fima.transaction.TransactionController
import fima.transactionservice.thriftscala.TransactionService$FinagleClient

object TransactionApi extends HttpServer {

  override protected def disableAdminHttpServer = true
  override val defaultFinatraHttpPort = ":8891"

  private val transactionServiceHost = flag(name = "transaction.service.host", default = "localhost", help = "Host for transaction-service")
  private val transactionServicePort = flag(name = "transaction.service.port", default = 9997, help = "Host for transaction-service")

  def transactionService: TransactionService$FinagleClient = new TransactionService$FinagleClient(
    thriftClientBuilder(transactionServiceHost(), transactionServicePort()), RichClientParam(
      protocolFactory = thrift.Protocols.binaryFactory(),
      "transactionService"
    )
  )

  private def thriftClientBuilder(host: String, port: Int) =
    ClientBuilder()
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
      .add(new TransactionController(transactionService))
  }

}