package fima.services.transaction.write.event

import fima.services.transaction.write.aggregate.BankAccount
import fima.services.transaction.write.aggregate.ClosedBankAccount
import fima.services.transaction.write.aggregate.OpenBankAccount
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("BankAccountSnapshotCreatedEvent")
data class BankAccountSnapshotCreatedEvent(override val version: Int, override val snapshotVersion: Int, val balanceInCents: Long) : Event(), EventVersion1 {

    override fun apply(aggregate: BankAccount): BankAccount {
        return if (aggregate.isOpen) OpenBankAccount(version, snapshotVersion, aggregate.accountNumber, balanceInCents)
        else ClosedBankAccount(version, snapshotVersion, aggregate.accountNumber)
    }

}