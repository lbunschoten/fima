package fima.services.transaction.write.event

import fima.services.transaction.write.aggregate.BankAccount
import fima.services.transaction.write.store.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@SerialName("MoneyDepositedEvent")
data class MoneyDepositedEvent(override val version: Int,
                               @Serializable(with=UUIDSerializer::class) val id: UUID,
                               val amountInCents: Long,
                               val date: Int,
                               val name: String,
                               val details: String,
                               val fromAccountNumber: String,
                               val toAccountNumber: String,
                               val type: String) : Event(), EventVersion1 {

  override fun apply(aggregate: BankAccount): BankAccount {
    return aggregate
      .withVersion(version)
      .withBalance(aggregate.balanceInCents + amountInCents)
  }

  fun fields(): Map<String, String> {
    return mapOf(
        "name" to name,
        "details" to details,
        "from" to fromAccountNumber,
        "to" to toAccountNumber,
        "price" to amountInCents.toString()
    )
  }
}