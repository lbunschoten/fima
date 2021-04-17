package fima.services.subscription

import fima.services.subscription.SubscriptionService.SubscriptionServiceGrpc.SubscriptionService
import fima.services.transaction.TransactionService.TransactionServiceGrpc
import io.grpc.Server
import io.grpc.netty.{NettyChannelBuilder, NettyServerBuilder}

import scala.concurrent.ExecutionContext
import scala.language.existentials

object SubscriptionServiceServer {

    private val port = 9997

  def main(args: Array[String]): Unit = {
        val server = new SubscriptionServiceServer(ExecutionContext.global)
        server.start()
        server.blockUntilShutdown()
    }
    
}

class SubscriptionServiceServer(executionContext: ExecutionContext) {

    private[this] var server: Server = _

    private def start(): Unit = {
      val transactionServiceHost = System.getenv("TRANSACTION_SERVICE_SERVICE_HOST")
      val transactionServicePort = System.getenv("TRANSACTION_SERVICE_SERVICE_PORT").toInt

      val channel = NettyChannelBuilder.forAddress(transactionServiceHost, transactionServicePort).usePlaintext().build()
      val transactionService: TransactionServiceGrpc.TransactionServiceBlockingStub = TransactionServiceGrpc.blockingStub(channel)

      server = NettyServerBuilder
          .forPort(SubscriptionServiceServer.port)
          .addService(SubscriptionService.bindService(new SubscriptionServiceImpl(transactionService), executionContext))
          .build.start

      println("Server started, listening on " + SubscriptionServiceServer.port)
        sys.addShutdownHook {
            System.err.println("*** shutting down gRPC server since JVM is shutting down")
            stop()
            System.err.println("*** server shut down")
        }
    }

    private def stop(): Unit = {
        if (server != null) {
            server.shutdown()
        }
    }

    private def blockUntilShutdown(): Unit = {
        if (server != null) {
            server.awaitTermination()
        }
    }

}