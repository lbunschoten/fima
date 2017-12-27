package fima.transactionservice

import com.twitter.finatra.thrift.ThriftServer
import com.twitter.finatra.thrift.filters._
import com.twitter.finatra.thrift.routing.ThriftRouter

object TransactionServiceServer extends ThriftServer {

  override val name = "transaction-service-server"
  override val disableAdminHttpServer = true
  override val defaultFinatraThriftPort = ":9997"

  override def configureThrift(router: ThriftRouter) {
    router
      .filter[LoggingMDCFilter]
      .filter[TraceIdMDCFilter]
      .filter[ThriftMDCFilter]
      .filter[AccessLoggingFilter]
      .filter[StatsFilter]
      .add(new TransactionService)
  }
}