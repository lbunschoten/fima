package fima.services.transaction.write.event

import fima.services.transaction.write.aggregate.BankAccount
import fima.services.transaction.write.aggregate.OnlineBankAccount
import kotlinx.serialization.Serializable

@Serializable
data class BankAccountOpenedEvent(override val version: Int, val accountNumber: String) : Event(), EventVersion1 {

  override fun apply(aggregate: BankAccount): BankAccount {
    return OnlineBankAccount(version, accountNumber, 0)
  }

}