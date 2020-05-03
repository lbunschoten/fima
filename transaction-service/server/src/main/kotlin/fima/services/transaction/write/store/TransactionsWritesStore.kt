package fima.services.transaction.write.store

import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.time.ZonedDateTime

interface TransactionsWritesStore {

  @SqlUpdate("""
    INSERT INTO Transactions(date, name, fromAccount, toAccount, type, amount)
    VALUES (:date, :name, :fromAccount, :toAccount, :type, :amountInCents)
  """)
  fun insertTransaction(date: ZonedDateTime, name: String, fromAccount: String, toAccount: String, type: String, amountInCents: Long)

}