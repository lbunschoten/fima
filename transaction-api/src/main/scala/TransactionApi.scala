import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import fima.services.transaction.TransactionServiceGrpc
import fima.services.transaction.TransactionServiceGrpc.TransactionServiceBlockingStub
import fima.services.transactionstatistics.TransactionStatisticsServiceGrpc
import fima.services.transactionstatistics.TransactionStatisticsServiceGrpc.TransactionStatisticsServiceBlockingStub
import fima.transaction.TransactionController
import io.grpc.ManagedChannelBuilder

object TransactionApi extends HttpServer {

  override protected def disableAdminHttpServer = true
  override val defaultFinatraHttpPort = ":80"

  private val transactionServiceHost = flag(name = "transaction.service.host", default = "localhost", help = "Host for transaction-service")
  private val transactionServicePort = flag(name = "transaction.service.port", default = 9997, help = "Host for transaction-service")

  private val transactionStatisticsServiceHost = flag(name = "transaction-statistics.service.host", default = "localhost", help = "Host for transaction-statistics-service")
  private val transactionStatisticsServicePort = flag(name = "transaction-statistics.service.port", default = 15001, help = "Host for transaction-statistics-service")

  def transactionService: TransactionServiceBlockingStub = {
    val channel = ManagedChannelBuilder.forAddress(transactionServiceHost(), transactionServicePort()).usePlaintext(true).build()
    TransactionServiceGrpc.newBlockingStub(channel)
  }

  def transactionStatisticsService: TransactionStatisticsServiceBlockingStub = {
    val channel = ManagedChannelBuilder.forAddress(transactionStatisticsServiceHost(), transactionStatisticsServicePort()).usePlaintext(true).build()
    TransactionStatisticsServiceGrpc.newBlockingStub(channel)
  }

  override protected def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add(new TransactionController(transactionService, transactionStatisticsService))
  }

}