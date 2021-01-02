package fima.api.tagging

import java.util.UUID
import fima.domain.transaction.TaggingRule as ProtoTaggingRule

data class TaggingRule(
    val id: UUID?,
    val regex: String,
    val tags: Set<String>
) {
    fun toProto(): ProtoTaggingRule {
        return ProtoTaggingRule
            .newBuilder()
            .setRegex(regex)
            .addAllTags(tags)
            .build()
    }
}

fun ProtoTaggingRule.simple(): TaggingRule {
    return TaggingRule(UUID.fromString(this.id), this.regex, this.tagsList.toSet())
}