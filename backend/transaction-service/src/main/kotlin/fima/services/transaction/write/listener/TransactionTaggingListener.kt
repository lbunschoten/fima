package fima.services.transaction.write.listener

import fima.services.transaction.write.event.Event
import fima.services.transaction.write.event.MoneyDepositedEvent
import fima.services.transaction.write.event.MoneyWithdrawnEvent
import fima.services.transaction.write.store.TransactionTagsWritesStore
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

class TransactionTaggingListener(
    private val transactionTagsWritesStore: TransactionTagsWritesStore,
    private val tagMatchers: Set<TagMatcher>
) : (Event) -> Unit {

    override fun invoke(event: Event) {
        tagMatchers.map { matcher ->
            matcher.matches(event)
        }

        when (event) {
            is MoneyWithdrawnEvent -> {
                val tags = tagWithdrawal(event)
                transactionTagsWritesStore.storeTags(event.id, tags)
            }
        }
    }

    private fun tagWithdrawal(event: MoneyWithdrawnEvent): Map<String, String> {
        if (event.name.contains("hypotheken")) {
            return mapOf(
                "tag1" to "tag1-value",
                "tag2" to "tag2-value"
            )
        }

        return emptyMap()
    }

}

@Polymorphic
interface MatcherType {
    fun matches(fields: List<String>): Boolean
}

@Serializable
@SerialName("contains")
class ContainsMatcherType(private val pattern: String) : MatcherType {
    override fun matches(fields: List<String>): Boolean {
        return fields.any { it.contains(pattern) }
    }
}

@Serializable
class TagMatcher(private val fields: List<String>, @Polymorphic private val matcher: MatcherType, private val tags: Set<String>) {

    fun matches(event: Event): Set<String> {
        val allFields = when (event) {
            is MoneyWithdrawnEvent -> event.fields()
            is MoneyDepositedEvent -> event.fields()
            else -> emptyMap()
        }

        return if (matcher.matches(allFields.filter { (key, _) -> fields.contains(key) }.values.toList())) tags
        else emptySet()
    }

}

fun main() {

    val matcherTypeSerializers = SerializersModule {
        polymorphic(MatcherType::class) {
            subclass(ContainsMatcherType::class)
        }
    }

    val json = Json { serializersModule = matcherTypeSerializers; classDiscriminator = "t" }
    val a = json.decodeFromString(ListSerializer(TagMatcher.serializer()), {}.javaClass.getResource("/tag-matchers.json").readText())
    println(a)

    val m = TagMatcher(listOf("name", "details"), ContainsMatcherType("test"), setOf("tags"))
    println(json.encodeToString(TagMatcher.serializer(), m))
}