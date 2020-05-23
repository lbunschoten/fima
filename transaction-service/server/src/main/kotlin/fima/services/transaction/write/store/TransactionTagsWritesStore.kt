package fima.services.transaction.write.store

import org.jdbi.v3.core.Handle
import java.util.UUID

class TransactionTagsWritesStore(
    private val handle: Handle
) {

    fun storeTags(transactionId: UUID, tags: Map<String, String>) {
        val insertTagsQuery = handle.prepareBatch("""
          INSERT INTO TransactionTags (id, transaction_id, key, value)
          VALUES (:id, :transaction_id, :key, :value)
          ON DUPLICATE KEY UPDATE
            value = :value
        """)

        tags.forEach { (key, value) ->
            insertTagsQuery
                .bind("id", UUID.randomUUID().toString())
                .bind("transaction_id", transactionId.toString())
                .bind("key", key)
                .bind("value", value)
                .add()
        }
        insertTagsQuery.execute()
    }
}