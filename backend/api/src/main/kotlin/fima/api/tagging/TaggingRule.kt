package fima.api.tagging

import fima.api.utils.FromProtoConvertable
import fima.api.utils.ToProtoConvertable
import fima.domain.transaction.taggingRule
import java.util.*
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
        val original = this
        return taggingRule {
            id = original.id?.toString() ?: ""
            regex = original.regex
            this.tags.addAll(tags)
        }
    }
}
