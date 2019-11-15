package fima.services.transaction.write

import fima.services.transaction.write.command.DepositMoneyCommand
import fima.services.transaction.write.command.OpenBankAccountCommand
import fima.services.transaction.write.command.WithdrawMoneyCommand
import io.grpc.stub.StreamObserver

class TransactionWritesServiceImpl(private val commandHandler: CommandHandler) : TransactionWritesServiceGrpc.TransactionWritesServiceImplBase() {

  override fun openBankAccount(request: OpenBankAccountRequest, responseObserver: StreamObserver<OpenBankAccountResponse>) {
    val errorMessages = commandHandler.processCommand(request.accountNumber, OpenBankAccountCommand(request.accountNumber, (request.initialBalance * 100).toLong()))

    val response = OpenBankAccountResponse
      .newBuilder()
      .addAllErrorMessages(errorMessages)
      .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }

  override fun deposit(request: DepositRequest, responseObserver: StreamObserver<DepositResponse>) {
    val errorMessages = commandHandler.processCommand(
      request.toAccount,
      DepositMoneyCommand(
        request.amountInCents,
        request.date,
        request.name,
        request.details,
        request.fromAccount,
        request.type
      )
    )

    val response = DepositResponse
      .newBuilder()
      .addAllErrorMessages(errorMessages)
      .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }

  override fun withdraw(request: WithdrawRequest, responseObserver: StreamObserver<WithdrawResponse>) {
    val errorMessages = commandHandler.processCommand(
      request.fromAccount,
      WithdrawMoneyCommand(
        request.amountInCents,
        request.date,
        request.name,
        request.details,
        request.toAccount,
        request.type
      )
    )

    val response = WithdrawResponse
      .newBuilder()
      .addAllErrorMessages(errorMessages)
      .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }
}