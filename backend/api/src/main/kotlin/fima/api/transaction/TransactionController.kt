package fima.api.transaction

import fima.services.transaction.ImportTransactionsRequest
import fima.services.transaction.TransactionServiceGrpcKt
import fima.services.transaction.importTransactionsRequest
import fima.services.transaction.tagTransactionsRequest
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.nio.charset.Charset

@RestController
class TransactionController @Autowired constructor(
    private val transactionService: TransactionServiceGrpcKt.TransactionServiceCoroutineStub
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/tag")
    suspend fun tagTransactions(): ResponseEntity<String> {
        logger.info("Received request for tagging all transactions")

        transactionService.tagTransactions(tagTransactionsRequest { })

        return ResponseEntity.ok("Successfully tagged all transactions")
    }

    @PutMapping("/transaction/import")
    suspend fun importTransactions(@RequestPart("transactions", required = true) transactions: Mono<FilePart>): ResponseEntity<String> {
        logger.info("Received import request")

        val request: ImportTransactionsRequest = transactions
            .flatMap {
                DataBufferUtils.join(it.content()).map { dataBuffer ->
                    dataBuffer.asInputStream().use { input ->
                        importTransactionsRequest {
                            this.transactions = String(input.readAllBytes(), Charset.forName("UTF-8"))
                        }
                    }
                }
            }.awaitSingle()

        transactionService.importTransactions(request)

        return ResponseEntity.ok("Upload successful")
    }
}