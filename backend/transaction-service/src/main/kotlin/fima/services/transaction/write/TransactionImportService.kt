package fima.services.transaction.write

import com.opencsv.bean.CsvBindByPosition
import com.opencsv.bean.CsvToBeanBuilder
import fima.services.transaction.write.command.DepositMoneyCommand
import fima.services.transaction.write.command.OpenBankAccountCommand
import fima.services.transaction.write.command.WithdrawMoneyCommand
import io.grpc.Status
import io.grpc.StatusException
import org.slf4j.LoggerFactory
import java.io.StringReader

interface TransactionImportService {
    suspend fun import(rawTransactions: String)
}

class TransactionImportServiceImpl(
    private val commandHandler: CommandHandler
) : TransactionImportService {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun import(rawTransactions: String) {
        logger.info("Received request for importing transactions")
        try {
            StringReader(rawTransactions).use { reader ->
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
        } catch (e: Exception) {
            logger.error("Failed to import transactions: ${e.message}")
            throw StatusException(Status.UNKNOWN.withCause(e))
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
