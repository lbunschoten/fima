package fima.services.transaction.write.event

import fima.services.transaction.write.aggregate.ClosedBankAccount
import fima.services.transaction.write.aggregate.OpenBankAccount
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.types.beOfType

class BankAccountClosedEventSpec : StringSpec() {

    init {
        val version = 0
        val snapshotVersion = 0
        val amountInCents = 100L
        val accountNumber = "from account number"

        val aggregate = OpenBankAccount(version, snapshotVersion, accountNumber, amountInCents)
        val event = BankAccountClosedEvent(version, snapshotVersion)

        "it should close a bank account" {
            event.apply(aggregate) should beOfType<ClosedBankAccount>()
        }

    }

}