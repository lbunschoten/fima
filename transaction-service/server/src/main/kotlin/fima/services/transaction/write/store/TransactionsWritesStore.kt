package fima.services.transaction.write.store

import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.time.ZonedDateTime
import java.util.UUID

interface TransactionsWritesStore {

  @SqlUpdate("""
    INSERT INTO Transactions(id, date, name, from_account, to_account, type, amount)
    VALUES (:id, :date, :name, :fromAccount, :toAccount, :type, :amountInCents)
  """)
  fun insertTransaction(id: UUID, date: ZonedDateTime, name: String, fromAccount: String, toAccount: String, type: String, amountInCents: Long)

}