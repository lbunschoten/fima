package fima.services.http.routes

import fima.services.transaction.write.TaggingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.tagRoutes(taggingService: TaggingService) {
    get("/transaction/tag") {
        taggingService.tagTransactions()
        call.respond(HttpStatusCode.OK)
    }
}