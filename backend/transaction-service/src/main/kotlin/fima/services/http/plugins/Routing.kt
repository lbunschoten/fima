import fima.services.http.routes.tagRoutes
import fima.services.http.routes.transactionRoutes
import fima.services.transaction.store.TransactionStatisticsStore
import fima.services.transaction.store.TransactionsStore
import fima.services.transaction.write.TaggingService
import fima.services.transaction.write.TransactionImportService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    transactionsStore: TransactionsStore,
    transactionStatisticsStore: TransactionStatisticsStore,
    transactionImportService: TransactionImportService,
    taggingService: TaggingService
) {
    routing {
        transactionRoutes(transactionsStore, transactionStatisticsStore, transactionImportService)
        tagRoutes(taggingService)
    }
}