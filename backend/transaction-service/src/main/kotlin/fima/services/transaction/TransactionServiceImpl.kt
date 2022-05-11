package fima.services.transaction

import com.opencsv.bean.CsvBindByPosition
import com.opencsv.bean.CsvToBeanBuilder
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
import java.io.StringReader
import java.time.LocalDate

class TransactionServiceImpl(
    private val transactionsStore: TransactionsStore,
    private val transactionStatisticsStore: TransactionStatisticsStore,
    private val taggingRuleStore: TaggingRulesStoreImpl,
    private val taggingService: TaggingService,
    private val commandHandler: CommandHandler
) : TransactionServiceGrpcKt.TransactionServiceCoroutineImplBase() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun importTransactions(request: ImportTransactionsRequest): ImportTransactionsResponse {
        logger.info("Received request for importing transactions")
        try {
            StringReader(request.transactions).use { reader ->
                val csvReader = CsvToBeanBuilder<TransactionRow>(reader)
                    .withSeparator(',')
                    .withQuoteChar('\"')
                    .withType(TransactionRow::class.java)
                    .withThrowExceptions(true)
                    .withSkipLines(1)
                    .build()

                val transactions = csvReader.parse()
                logger.info("Importing ${transactions.size} transactions")

                transactions.forEachIndexed { index, transaction ->
                    if (index == 0) {
                        openBankAccount(
                            accountNumber = if (transaction.direction == "Af") transaction.firstAccount else transaction.secondAccount,
                            initialBalance = 5000F // FIXME: Make configurable
                        )
                    }

                    val validationErrors = if (transaction.direction == "Af") {
                        withdraw(
                            amountInCents = (transaction.amount.replace(',', '.').toFloat() * 100).toLong(),
                            date = transaction.date,
                            details = transaction.details,
                            name = transaction.name,
                            fromAccount = transaction.firstAccount,
                            toAccount = transaction.secondAccount,
                            type = transaction.type
                        )
                    } else {
                        deposit(
                            amountInCents = (transaction.amount.replace(',', '.').toFloat() * 100).toLong(),
                            date = transaction.date,
                            details = transaction.details,
                            name = transaction.name,
                            fromAccount = transaction.secondAccount,
                            toAccount = transaction.firstAccount,
                            type = transaction.type
                        )
                    }

                    validationErrors.forEach { logger.warn(it) }
                }
            }

            return importTransactionsResponse {}
        } catch (e: Exception) {
            logger.error("Failed to import transactions: ${e.message}")
            throw StatusException(Status.UNKNOWN.withCause(e))
        }
    }

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

    private fun openBankAccount(accountNumber: String, initialBalance: Float): Set<String> {
        logger.info("Received request for opening bank account $accountNumber")
        return commandHandler.processCommand(accountNumber, OpenBankAccountCommand(accountNumber, (initialBalance * 100).toLong()))
    }

    private fun withdraw(fromAccount: String, amountInCents: Long, date: Int, name: String, details: String, toAccount: String, type: String): Set<String> {
        logger.info("Received request for withdrawal from $fromAccount")
        return commandHandler.processCommand(
            fromAccount,
            WithdrawMoneyCommand(amountInCents, date, name, details, toAccount, type)
        )
    }

    private fun deposit(toAccount: String, amountInCents: Long, date: Int, name: String, details: String, fromAccount: String, type: String): Set<String> {
        logger.info("Received request for deposit to $toAccount")

        return commandHandler.processCommand(
            toAccount,
            DepositMoneyCommand(amountInCents, date, name, details, fromAccount, type)
        )
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

data class TransactionRow(
    @CsvBindByPosition(position = 0) val date: Int = 0,
    @CsvBindByPosition(position = 1) val name: String = "",
    @CsvBindByPosition(position = 2) val firstAccount: String = "",
    @CsvBindByPosition(position = 3) val secondAccount: String = "",
    @CsvBindByPosition(position = 4) val type: String = "",
    @CsvBindByPosition(position = 5) val direction: String = "",
    @CsvBindByPosition(position = 6) val amount: String = "",
    @CsvBindByPosition(position = 8) val details: String = ""
)
