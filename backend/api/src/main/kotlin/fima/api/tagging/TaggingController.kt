package fima.api.tagging

import fima.services.transaction.GetTaggingRulesRequest
import fima.services.transaction.StoreTaggingRuleRequest
import fima.services.transaction.TransactionServiceGrpcKt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TaggingController @Autowired constructor(
    private val transactionService: TransactionServiceGrpcKt.TransactionServiceCoroutineStub,
) {

    @GetMapping("/tagging-rules")
    suspend fun getTaggingRules(): Set<TaggingRule> {
        val request = GetTaggingRulesRequest.newBuilder().build()

        return transactionService
            .getTaggingRules(request)
            .taggingRulesList
            .map { it.simple() }
            .toSet()
    }

    @PutMapping("/tagging-rule")
    suspend fun addTagRule(@RequestBody(required = true) taggingRules: Set<TaggingRule>): ResponseEntity<String> {
        val request = StoreTaggingRuleRequest
            .newBuilder()
            .addAllTaggingRules(taggingRules.map { it.toProto() })
            .build()

        transactionService.storeTaggingRule(request)

        return ResponseEntity.ok("Added tagging rule successfully")
    }
}

