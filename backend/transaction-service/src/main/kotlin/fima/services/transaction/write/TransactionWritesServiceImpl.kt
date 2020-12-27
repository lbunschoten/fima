package fima.services.transaction.write

import fima.services.transaction.write.command.DepositMoneyCommand
import fima.services.transaction.write.command.OpenBankAccountCommand
import fima.services.transaction.write.command.WithdrawMoneyCommand
import org.slf4j.LoggerFactory

class TransactionWritesServiceImpl(private val commandHandler: CommandHandler) : TransactionWritesServiceGrpcKt.TransactionWritesServiceCoroutineImplBase() {

  private val logger = LoggerFactory.getLogger(javaClass)

  override suspend fun openBankAccount(request: OpenBankAccountRequest): OpenBankAccountResponse {
    logger.info("Received request for opening bank account ${request.accountNumber}")
    val errorMessages = commandHandler.processCommand(request.accountNumber, OpenBankAccountCommand(request.accountNumber, (request.initialBalance * 100).toLong()))

    return OpenBankAccountResponse
      .newBuilder()
      .addAllErrorMessages(errorMessages)
      .build()
  }

  override suspend fun withdraw(request: WithdrawRequest): WithdrawResponse {
    logger.info("Received request for withdrawal from ${request.fromAccount}")
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

    return WithdrawResponse
        .newBuilder()
        .addAllErrorMessages(errorMessages)
        .build()
  }

  override suspend fun deposit(request: DepositRequest): DepositResponse {
    logger.info("Received request for deposit to ${request.toAccount}")

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

    return DepositResponse
        .newBuilder()
        .addAllErrorMessages(errorMessages)
        .build()
  }

}