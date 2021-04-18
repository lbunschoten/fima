package fima.services.transaction

import fima.services.transaction.store.TaggingRulesStoreImpl
import fima.services.transaction.store.TransactionStatisticsStore
import fima.services.transaction.store.TransactionsStore
import fima.services.transaction.write.CommandHandler
import fima.services.transaction.write.TaggingService
import fima.services.transaction.write.command.DepositMoneyCommand
import fima.services.transaction.write.command.OpenBankAccountCommand
import fima.services.transaction.write.command.WithdrawMoneyCommand
import fima.services.utils.ProtoUtils.toProto
import io.grpc.Status
import io.grpc.StatusException
import org.slf4j.LoggerFactory

class TransactionServiceImpl(
    private val transactionsStore: TransactionsStore,
    private val transactionStatisticsStore: TransactionStatisticsStore,
    private val taggingRuleStore: TaggingRulesStoreImpl,
    private val taggingService: TaggingService,
    private val commandHandler: CommandHandler
) : TransactionServiceGrpcKt.TransactionServiceCoroutineImplBase() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun searchTransactions(request: SearchTransactionsRequest): SearchTransactionsResponse {
        logger.info("Received search request for transactions: $request")
        val transactions = transactionsStore.searchTransactions(
            query = request.query.takeIf { it.isNullOrBlank() },
            filters = request.filtersList.map { filter ->
                filter.tagsList.map { t -> t.key to t.value }
            }
        )

        return SearchTransactionsResponse
            .newBuilder()
            .addAllTransaction(transactions.map { it.toProto() })
            .build()
    }

    override suspend fun getTransaction(request: GetTransactionRequest): GetTransactionResponse {
        logger.info("Received request for transaction ${request.id}")
        return GetTransactionResponse
            .newBuilder()
            .setTransaction(
                transactionsStore
                    .getById(request.id)
                    .toProto()
            )
            .build()
    }

    override suspend fun getRecentTransactions(request: GetRecentTransactionsRequest): GetRecentTransactionResponse {
        logger.info("Received request for recent transactions")

        return try {
            GetRecentTransactionResponse
                .newBuilder()
                .addAllTransactions(
                    transactionsStore
                        .getRecent(request.offset, request.limit)
                        .map { it.toProto() }
                )
                .build()
        } catch (e: Exception) {
            logger.error("Failed retrieving getting recent transactions: ${e.message}")
            throw StatusException(Status.UNKNOWN.withCause(e))
        }
    }

    override suspend fun getMonthlyStatistics(request: TransactionsStatisticsRequest): TransactionStatisticsResponse {
        logger.info("Received request for monthly transaction statistics")

        return TransactionStatisticsResponse
            .newBuilder()
            .addAllMonthlyStatistics(
                transactionStatisticsStore
                    .getMonthlyStatistics(request.startDate.month, request.startDate.year, request.endDate.month, request.endDate.year)
                    .map { it.toProto() }
            )
            .build()
    }

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

    override suspend fun getTaggingRules(request: GetTaggingRulesRequest): GetTaggingRulesResponse {
        logger.info("Received request for tagging rules")

        val taggingRules = try {
            taggingRuleStore.getTaggingRules()
        } catch (e: Exception) {
            logger.error(e.message)
            emptyList()
        }

        return GetTaggingRulesResponse
            .newBuilder()
            .addAllTaggingRules(taggingRules.toProto())
            .build()
    }

    override suspend fun storeTaggingRule(request: StoreTaggingRuleRequest): StoreTaggingRuleResponse {
        logger.info("Received request to add tagging rule")

        val response = StoreTaggingRuleResponse.newBuilder()

        try {
            request.taggingRulesList.forEach { taggingRule ->
                taggingRuleStore.storeTaggingRule(taggingRule)
            }
        } catch (e: Exception) {
            logger.error("Could not store tagging rule: ${e.message}")
            response.addErrorMessages(e.message)
        }

        return response.build()
    }

    override suspend fun tagTransactions(request: TagTransactionsRequest): TagTransactionsResponse {
        logger.info("Received request to tag all transactions")

        taggingService.tagTransactions()

        return TagTransactionsResponse.newBuilder().build()
    }
}