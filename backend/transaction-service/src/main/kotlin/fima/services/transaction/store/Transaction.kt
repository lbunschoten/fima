package fima.services.transaction.store

import fima.domain.transaction.Date
import fima.services.utils.ToProtoConvertable
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.time.LocalDate
import java.util.UUID
import fima.domain.transaction.Transaction as ProtoTransaction

data class Transaction(
    val id: UUID,
    val type: TransactionType,
    val date: LocalDate,
    val name: String,
    val fromAccount: String,
    val toAccount: String,
    val amount: Float,
    val tags: Map<String, String>
) : ToProtoConvertable<ProtoTransaction> {

    override fun toProto(): ProtoTransaction {
        return ProtoTransaction
            .newBuilder()
            .setId(id.toString())
            .setType(type.toProto())
            .setDate(Date.newBuilder().setDay(date.dayOfMonth).setMonth(date.monthValue).setYear(date.year).build())
            .setName(name)
            .setFromAccount(fromAccount)
            .setToAccount(toAccount)
            .setAmount(amount)
            .putAllTags(tags)
            .build()
    }

}

class TransactionRowMapper : RowMapper<Transaction> {
    override fun map(rs: ResultSet, ctx: StatementContext): Transaction {
        val tags = rs
                .getString("tags")
                ?.takeIf { it.isNotBlank() }
                ?.split(",")
                ?.associate { tag ->
                    val (key, value) = tag.split(":")
                    Pair(key, value)
                }
                ?: emptyMap()

        return Transaction(
            id = UUID.fromString(rs.getString("id")),
            type = TransactionType.of(rs.getString("type")),
            date = rs.getDate("date").toLocalDate(),
            name = rs.getString("name"),
            fromAccount = rs.getString("from_account"),
            toAccount = rs.getString("to_account"),
            amount = rs.getFloat("amount"),
            tags = tags
        )
    }
}

