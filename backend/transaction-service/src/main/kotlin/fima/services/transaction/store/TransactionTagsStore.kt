package fima.services.transaction.store

import org.jdbi.v3.core.Jdbi
import java.io.Closeable
import java.util.UUID

class TransactionTagsStore(db: Jdbi) : Closeable {

    private val handle = db.open()

    fun storeTags(transactionId: UUID, tags: Map<String, String>) {
        val insertTagsQuery = handle.prepareBatch("""
          INSERT INTO TransactionTags (`id`, `transaction_id`, `key`, `value`)
          VALUES (:id, :transaction_id, :key, :value)
          ON DUPLICATE KEY UPDATE `value` = :value
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

    override fun close() = handle.close()
}