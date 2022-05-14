package fima.services.transaction.write.event

import fima.services.transaction.store.UUIDSerializer
import fima.services.transaction.write.aggregate.BankAccount
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@SerialName("MoneyDepositedEvent")
data class MoneyDepositedEvent(override val version: Int,
                               override val snapshotVersion: Int,
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
            .withSnapshotVersion(snapshotVersion)
            .withBalance(aggregate.balanceInCents + amountInCents)
    }

}