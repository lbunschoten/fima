package fima.services.http.routes

import fima.services.transaction.store.TransactionStatisticsStore
import fima.services.transaction.store.TransactionsStore
import fima.services.transaction.store.UUIDSerializer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.time.format.DateTimeFormatter
import java.util.*

@Serializable
data class MonthlyTransactionStatisticsDto(
    val month: Int,
    val year: Int,
    val transactions: Int,
    val sum: Float,
    val balance: Float
)

@Serializable
data class TransactionDto(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val date: String,
    val type: String,
    val name: String,
    val toAccount: String,
    val fromAccount: String,
    val amount: Float,
    val tags: Map<String, String>
)

fun Route.transactionRoutes(transactionsStore: TransactionsStore, transactionStatisticsStore: TransactionStatisticsStore) {
    get("/transaction/statistics") {
        call.respond(HttpStatusCode.OK, transactionStatisticsStore.getMonthlyStatistics(1, 2020, 12, 2020).map {
            MonthlyTransactionStatisticsDto(it.month, it.year, it.numTransactions, it.sum.toFloat(), it.balance.toFloat())
        })
    }

    get("/transaction/recent") {
        val offset = call.parameters["offset"]!!.toInt()
        val limit = call.parameters["limit"]!!.toInt()
        call.respond(HttpStatusCode.OK, transactionsStore.getRecent(offset, limit).map {
            val transactionDate = it.date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            TransactionDto(it.id, transactionDate, it.type.toString(), it.name, it.toAccount, it.fromAccount, it.amount, it.tags)
        })
    }
}