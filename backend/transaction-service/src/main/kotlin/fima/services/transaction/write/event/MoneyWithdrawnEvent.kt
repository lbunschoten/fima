package fima.services.transaction.write.event

import fima.services.transaction.store.UUIDSerializer
import fima.services.transaction.write.aggregate.BankAccount
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@SerialName("MoneyWithdrawnEvent")
data class MoneyWithdrawnEvent(override val version: Int,
                               @Serializable(with = UUIDSerializer::class) override val id: UUID,
                               override val amountInCents: Long,
                               override val date: Int,
                               override val name: String,
                               override val details: String,
                               override val fromAccountNumber: String,
                               override val toAccountNumber: String,
                               override val type: String) : TransactionEvent, Event(), EventVersion1 {

    override fun apply(aggregate: BankAccount): BankAccount {
        return aggregate
            .withVersion(version)
            .withBalance(aggregate.balanceInCents - amountInCents)
    }

}