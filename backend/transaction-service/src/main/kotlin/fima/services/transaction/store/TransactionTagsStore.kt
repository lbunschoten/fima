package fima.services.transaction.store

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import java.util.*

class TransactionTagsStore(private val db: Jdbi) {

    fun storeTags(transactionId: UUID, tags: Map<String, String>) {
        db.withHandleUnchecked { handle ->
            val insertTagsQuery = handle.prepareBatch("""
              INSERT INTO transaction_tags (id, transaction_id, key, value)
              VALUES (:id, :transaction_id, :key, :value)
              ON CONFLICT(id) DO UPDATE SET value = :value
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

    fun deleteTags() {
        db.withHandleUnchecked { handle ->
            handle
                .createUpdate("""DELETE FROM transaction_tags""")
                .execute()
        }
    }
}