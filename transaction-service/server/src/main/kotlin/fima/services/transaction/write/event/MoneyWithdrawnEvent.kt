package fima.services.transaction.write.event

import fima.services.transaction.write.aggregate.BankAccount
import kotlinx.serialization.Serializable

@Serializable
data class MoneyWithdrawnEvent(override val version: Int, val amountInCents: Long) : Event(), EventVersion1 {

  override fun apply(aggregate: BankAccount): BankAccount {
    return aggregate
      .withVersion(version)
      .withBalance(aggregate.balanceInCents + amountInCents)
  }
}