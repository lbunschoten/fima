import fima.services.http.routes.tagRoutes
import fima.services.http.routes.transactionRoutes
import fima.services.transaction.store.TransactionsStore
import fima.services.transaction.write.TaggingService
import fima.services.transaction.write.TransactionImportService
import fima.services.transaction.write.TransactionStatisticsService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    transactionsStore: TransactionsStore,
    transactionImportService: TransactionImportService,
    transactionStatisticsService: TransactionStatisticsService,
    taggingService: TaggingService
) {
    routing {
        transactionRoutes(transactionsStore, transactionStatisticsService, transactionImportService)
        tagRoutes(taggingService)
    }
}