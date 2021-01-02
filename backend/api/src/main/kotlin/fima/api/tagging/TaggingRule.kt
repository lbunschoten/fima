package fima.api.tagging

import fima.api.utils.FromProtoConvertable
import fima.api.utils.ToProtoConvertable
import java.util.UUID
import fima.domain.transaction.TaggingRule as ProtoTaggingRule

data class TaggingRule(
    val id: UUID?,
    val regex: String,
    val tags: Set<String>
): ToProtoConvertable<ProtoTaggingRule> {

    companion object: FromProtoConvertable<ProtoTaggingRule, TaggingRule> {
        override fun fromProto(proto: ProtoTaggingRule): TaggingRule {
            return TaggingRule(UUID.fromString(proto.id), proto.regex, proto.tagsList.toSet())
        }
    }

    override fun toProto(): ProtoTaggingRule {
        return ProtoTaggingRule
            .newBuilder()
            .setId(id?.toString() ?: "")
            .setRegex(regex)
            .addAllTags(tags)
            .build()
    }
}
