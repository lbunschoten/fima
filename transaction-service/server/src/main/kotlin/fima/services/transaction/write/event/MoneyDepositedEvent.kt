package fima.services.transaction.write.event

import fima.services.transaction.write.aggregate.BankAccount
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("MoneyDepositedEvent")
data class MoneyDepositedEvent(override val version: Int,
                               val amountInCents: Long,
                               val date: Int,
                               val name: String,
                               val details: String,
                               val accountNumber: String,
                               val type: String) : Event(), EventVersion1 {

  override fun apply(aggregate: BankAccount): BankAccount {
    return aggregate
      .withVersion(version)
      .withBalance(aggregate.balanceInCents + amountInCents)
  }
}