package fima.services.transaction

import fima.events.transaction.TransactionAddedEvent
import fima.services.transaction.conversion.RawTransactionToTransactionConverter
import fima.services.transaction.events.TransactionEventProducer
import fima.services.transaction.repository.TransactionsRepository
import io.grpc.stub.StreamObserver

class TransactionServiceImpl(private val transactionsRepository: TransactionsRepository,
                             private val transactionEventProducer: TransactionEventProducer,
                             private val toTransactionConverter: RawTransactionToTransactionConverter) : TransactionServiceGrpc.TransactionServiceImplBase() {

  override fun getTransaction(request: GetTransactionRequest, responseObserver: StreamObserver<GetTransactionResponse>) {
    val response = GetTransactionResponse
      .newBuilder()
      .setTransaction(transactionsRepository.getById(request.id).toProto())
      .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }

  override fun getRecentTransactions(request: GetRecentTransactionsRequest, responseObserver: StreamObserver<GetRecentTransactionResponse>) {
    val response = GetRecentTransactionResponse
      .newBuilder()
      .addAllTransactions(transactionsRepository.getRecent().map { it.toProto() })
      .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }

  override fun insertTransaction(request: InsertTransactionRequest, responseObserver: StreamObserver<InsertTransactionResponse>) {
    val transaction = toTransactionConverter(request.transaction)

    transactionsRepository.insertTransaction(transaction)

    transactionEventProducer.produce(
      TransactionAddedEvent
        .newBuilder()
        .setTransaction(transaction)
        .build()
    )

    val response = InsertTransactionResponse.newBuilder().build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }
}