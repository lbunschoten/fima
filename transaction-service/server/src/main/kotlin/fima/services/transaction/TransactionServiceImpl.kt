package fima.services.transaction

import fima.domain.transaction.Transaction
import fima.events.transaction.TransactionAddedEvent
import fima.services.transaction.conversion.RawTransactionToTransactionConverter
import fima.services.transaction.events.TransactionEventProducer
import fima.services.transaction.repository.TransactionsRepository
import io.grpc.stub.StreamObserver

class TransactionServiceImpl(private val transactionsRepository: TransactionsRepository,
                             private val transactionEventProducer: TransactionEventProducer,
                             private val toTransactionConverter: RawTransactionToTransactionConverter) : TransactionServiceGrpc.TransactionServiceImplBase() {

    override fun getTransaction(request: GetTransactionRequest, responseObserver: StreamObserver<GetTransactionResponse>) {
        println("DEBUG: Get transaction")
        val response = GetTransactionResponse
                .newBuilder()
                .setTransaction(Transaction.newBuilder().setId(1))
                .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun getRecentTransactions(request: GetRecentTransactionsRequest, responseObserver: StreamObserver<GetRecentTransactionResponse>) {
        val response = GetRecentTransactionResponse
                .newBuilder()
                .addTransactions(Transaction.newBuilder().setId(1))
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