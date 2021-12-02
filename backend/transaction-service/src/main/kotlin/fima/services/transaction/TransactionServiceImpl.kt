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
        try {
            logger.info("Received search request for transactions: $request")
            val transactions = transactionsStore.searchTransactions(
                request.filtersList.map { f ->
                    TransactionsStore.SearchFilters(
                        queryFilter = f?.query?.queryString?.takeIf { it.isNotBlank() },
                        tagFilters = f.tagsList.associate { filter -> filter.key to filter.value }
                    )
                }
            )

            logger.info("Found ${transactions.size} after search request")

            return searchTransactionsResponse {
                this.transactions.addAll(transactions.map { it.toProto() })
            }
        } catch (e: Exception) {
            logger.error("Failed to search for transactions: ${e.message}")
            throw StatusException(Status.UNKNOWN.withCause(e))
        }
    }

    override suspend fun getTransaction(request: GetTransactionRequest): GetTransactionResponse {
        logger.info("Received request for transaction ${request.id}")
        return getTransactionResponse {
            transaction = transactionsStore.getById(request.id).toProto()
        }
    }

    override suspend fun getRecentTransactions(request: GetRecentTransactionsRequest): GetRecentTransactionResponse {
        logger.info("Received request for recent transactions")

        return try {
            getRecentTransactionResponse {
                this.transactions.addAll(
                    transactionsStore
                        .getRecent(request.offset, request.limit)
                        .map { it.toProto() }
                )
            }
        } catch (e: Exception) {
            logger.error("Failed retrieving getting recent transactions: ${e.message}")
            throw StatusException(Status.UNKNOWN.withCause(e))
        }
    }

    override suspend fun getMonthlyStatistics(request: TransactionsStatisticsRequest): TransactionStatisticsResponse {
        logger.info("Received request for monthly transaction statistics")

        return transactionStatisticsResponse {
            this.monthlyStatistics.addAll(
                transactionStatisticsStore
                    .getMonthlyStatistics(request.startDate.month, request.startDate.year, request.endDate.month, request.endDate.year)
                    .map { it.toProto() }
            )
        }
    }

    override suspend fun openBankAccount(request: OpenBankAccountRequest): OpenBankAccountResponse {
        logger.info("Received request for opening bank account ${request.accountNumber}")
        val errorMessages = commandHandler.processCommand(request.accountNumber, OpenBankAccountCommand(request.accountNumber, (request.initialBalance * 100).toLong()))

        return openBankAccountResponse {
            this.errorMessages.addAll(errorMessages)
        }
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

        return withdrawResponse {
            this.errorMessages.addAll(errorMessages)
        }
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

        return depositResponse {
            this.errorMessages.addAll(errorMessages)
        }
    }

    override suspend fun getTaggingRules(request: GetTaggingRulesRequest): GetTaggingRulesResponse {
        logger.info("Received request for tagging rules")

        val taggingRules = try {
            taggingRuleStore.getTaggingRules()
        } catch (e: Exception) {
            logger.error(e.message)
            emptyList()
        }

        return getTaggingRulesResponse {
            this.taggingRules.addAll(taggingRules.toProto())
        }
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

        try {
            taggingService.tagTransactions()
        } catch (e: Exception) {
            logger.error("Could not tag transactions: ${e.message}")
        }

        return tagTransactionsResponse {}
    }
}