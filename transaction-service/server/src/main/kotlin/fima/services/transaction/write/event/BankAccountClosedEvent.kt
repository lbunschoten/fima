package fima.services.transaction.write.event

import fima.services.transaction.write.aggregate.BankAccount
import fima.services.transaction.write.aggregate.ClosedBankAccount
import kotlinx.serialization.Serializable

@Serializable
data class BankAccountClosedEvent(override val version: Int) : Event(), EventVersion1 {

  override fun apply(aggregate: BankAccount): BankAccount {
    return ClosedBankAccount(version, aggregate.accountNumber)
  }
}