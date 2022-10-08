package fima.services.transaction.write.event

import fima.services.transaction.write.aggregate.ClosedBankAccount
import fima.services.transaction.write.aggregate.OpenBankAccount
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BankAccountSnapshotCreatedEventSpec : StringSpec() {

    init {
        val version = 0
        val snapshotVersion = 0
        val accountNumber = "from account number"
        val balanceInCents = 100L
        val bankAccount = OpenBankAccount(version, snapshotVersion, accountNumber, balanceInCents)
        val openAccountSnapshot = BankAccountSnapshotCreatedEvent(version, snapshotVersion, balanceInCents, isOpen = true)
        val closedAccountSnapshot = BankAccountSnapshotCreatedEvent(version, snapshotVersion, balanceInCents, isOpen = false)

        "it should create an open bank account aggregate from an open account snapshot" {
            openAccountSnapshot.apply(bankAccount) shouldBe OpenBankAccount(
                version = version,
                snapshotVersion = snapshotVersion,
                accountNumber = accountNumber,
                balanceInCents = balanceInCents,
            )
        }

        "it should create a closed bank account aggregate from a closed account snapshot" {
            closedAccountSnapshot.apply(bankAccount) shouldBe ClosedBankAccount(
                version = version,
                snapshotVersion = snapshotVersion,
                accountNumber = accountNumber,
            )
        }

    }

}