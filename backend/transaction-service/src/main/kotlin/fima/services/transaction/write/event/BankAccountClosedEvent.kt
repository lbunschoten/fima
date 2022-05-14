package fima.services.transaction.write.event

import fima.services.transaction.write.aggregate.BankAccount
import fima.services.transaction.write.aggregate.ClosedBankAccount
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("BankAccountClosedEvent")
data class BankAccountClosedEvent(override val version: Int, override val snapshotVersion: Int) : Event(), EventVersion1 {

    override fun apply(aggregate: BankAccount): BankAccount {
        return ClosedBankAccount(version, snapshotVersion, aggregate.accountNumber)
    }
}