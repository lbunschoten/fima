package fima.services.transaction.read.store

import fima.domain.transaction.Date
import fima.domain.transaction.Transaction
import fima.services.transaction.conversion.RawTypeToTransactionTypeConverter
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

class TransactionMapper : RowMapper<Transaction> {
  override fun map(rs: ResultSet, ctx: StatementContext): Transaction {
    val date = rs.getDate("date").toLocalDate()
    return Transaction
      .newBuilder()
      .setId(rs.getInt("id"))
      .setType(RawTypeToTransactionTypeConverter()(rs.getString("type")))
      .setDate(Date.newBuilder().setDay(date.dayOfMonth).setMonth(date.monthValue).setYear(date.year).build())
      .setName(rs.getString("name"))
      .setFromAccount(rs.getString("from_account"))
      .setToAccount(rs.getString("to_account"))
      .setAmount(rs.getFloat("amount"))
      .build()
  }
}

interface TransactionReads {

  @SqlQuery("SELECT * FROM Transactions WHERE id = :id")
  @RegisterRowMapper(TransactionMapper::class)
  fun getById(id: Int): Transaction

  @SqlQuery("SELECT * FROM Transactions LIMIT :limit OFFSET :offset")
  @RegisterRowMapper(TransactionMapper::class)
  fun getRecent(offset: Int, limit: Int): List<Transaction>
}