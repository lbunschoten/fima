import fima.services.http.routes.transactionRoutes
import fima.services.transaction.store.TransactionStatisticsStore
import fima.services.transaction.store.TransactionsStore
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(transactionsStore: TransactionsStore, transactionStatisticsStore: TransactionStatisticsStore) {
    routing {
        transactionRoutes(transactionsStore, transactionStatisticsStore)
    }
}