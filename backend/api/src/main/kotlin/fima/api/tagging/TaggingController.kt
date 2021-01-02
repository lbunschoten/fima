package fima.api.tagging

import fima.services.transaction.GetTaggingRulesRequest
import fima.services.transaction.StoreTaggingRuleRequest
import fima.services.transaction.TransactionServiceGrpcKt
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TaggingController @Autowired constructor(
    private val transactionService: TransactionServiceGrpcKt.TransactionServiceCoroutineStub,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/tagging-rules", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getTaggingRules(): Set<TaggingRule> {
        logger.info("Received request for retrieving tagging rules")

        val request = GetTaggingRulesRequest.newBuilder().build()

        return transactionService
            .getTaggingRules(request)
            .taggingRulesList
            .map { it.simple() }
            .toSet()
    }

    @PutMapping("/tagging-rule", consumes = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun putTaggingRule(@RequestBody(required = true) taggingRule: TaggingRule): ResponseEntity<String> {
        logger.info("Received request for storing a tagging rule")

        val request = StoreTaggingRuleRequest
            .newBuilder()
            .addTaggingRules(taggingRule.toProto())
            .build()

        transactionService.storeTaggingRule(request)

        return ResponseEntity.ok("Added tagging rule successfully")
    }
}

