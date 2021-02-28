package fima.services.subscription

import fima.services.subscription.SubscriptionService.SubscriptionServiceGrpc.SubscriptionService
import io.grpc.{Server, ServerBuilder}

import scala.concurrent.ExecutionContext

object SubscriptionServiceServer {

    private val port = 9997

    @main def start: Unit = {
        val server = new SubscriptionServiceServer(ExecutionContext.global)
        server.start()
        server.blockUntilShutdown()
    }
    
}

class SubscriptionServiceServer(executionContext: ExecutionContext) {
    self =>
    
    private[this] var server: Server = null

    private def start(): Unit = {
        server = ServerBuilder.forPort(SubscriptionServiceServer.port).addService(SubscriptionService.bindService(new SubscriptionServiceImpl, executionContext)).build.start
        println("Server started, listening on " + SubscriptionServiceServer.port)
        sys.addShutdownHook {
            System.err.println("*** shutting down gRPC server since JVM is shutting down")
            self.stop()
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