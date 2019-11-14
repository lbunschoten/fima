package fima.services.transaction.write

import fima.services.transaction.write.command.DepositMoneyCommand
import fima.services.transaction.write.command.OpenBankAccountCommand
import io.grpc.stub.StreamObserver

class TransactionWritesServiceImpl(private val commandHandler: CommandHandler) : TransactionWritesServiceGrpc.TransactionWritesServiceImplBase() {

  override fun openBankAccount(request: OpenBankAccountRequest, responseObserver: StreamObserver<OpenBankAccountResponse>) {
    val errorMessages = commandHandler.processCommand(request.accountNumber, OpenBankAccountCommand(request.accountNumber))

    val response = OpenBankAccountResponse
      .newBuilder()
      .addAllErrorMessages(errorMessages)
      .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }

  override fun deposit(request: DepositRequest, responseObserver: StreamObserver<DepositResponse>) {
    val errorMessages = commandHandler.processCommand(request.toAccount, DepositMoneyCommand((request.amount * 100).toLong()))

    val response = DepositResponse
      .newBuilder()
      .addAllErrorMessages(errorMessages)
      .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }

  override fun withdraw(request: WithdrawalRequest, responseObserver: StreamObserver<WithdrawalResponse>) {
    val errorMessages = commandHandler.processCommand(request.fromAccount, DepositMoneyCommand((request.amount * 100).toLong()))

    val response = WithdrawalResponse
      .newBuilder()
      .addAllErrorMessages(errorMessages)
      .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }
}