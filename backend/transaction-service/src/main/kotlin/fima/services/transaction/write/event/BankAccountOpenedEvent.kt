package fima.services.transaction.write.event

import fima.services.transaction.write.aggregate.BankAccount
import fima.services.transaction.write.aggregate.OpenBankAccount
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("BankAccountOpenedEvent")
data class BankAccountOpenedEvent(override val version: Int, override val snapshotVersion: Int, val accountNumber: String, val initialBalanceInCents: Long) : Event(), EventVersion1 {

    override fun apply(aggregate: BankAccount): BankAccount {
        return OpenBankAccount(version, snapshotVersion, accountNumber, initialBalanceInCents)
    }

}