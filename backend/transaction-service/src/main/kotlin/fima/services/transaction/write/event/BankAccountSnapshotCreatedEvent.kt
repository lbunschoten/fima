package fima.services.transaction.write.event

import fima.services.transaction.write.aggregate.BankAccount
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("BankAccountSnapshotCreatedEvent")
data class BankAccountSnapshotCreatedEvent(override val version: Int, override val snapshotVersion: Int, val balanceInCents: Long) : Event(), EventVersion1 {

    override fun apply(aggregate: BankAccount): BankAccount {
        return aggregate
            .withVersion(version)
            .withSnapshotVersion(snapshotVersion)
            .withBalance(balanceInCents)
    }

}