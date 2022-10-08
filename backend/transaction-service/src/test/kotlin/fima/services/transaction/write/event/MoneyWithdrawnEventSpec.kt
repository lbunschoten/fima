package fima.services.transaction.write.event

import fima.services.transaction.write.aggregate.ClosedBankAccount
import fima.services.transaction.write.aggregate.OpenBankAccount
import fima.services.transaction.write.aggregate.UniniatilizedAccount
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.lang.IllegalStateException
import java.time.LocalDate
import java.util.*

class MoneyWithdrawnEventSpec : StringSpec() {

    init {
        val version = 0
        val snapshotVersion = 0
        val amountInCents = 100L
        val date = LocalDate.now()
        val name = "name"
        val details = "details"
        val fromAccountNumber = "from account number"
        val toAccountNumber = "to account number"
        val type = "type"

        val uninitializedBankAccount = UniniatilizedAccount(fromAccountNumber)
        val openBankAccount = OpenBankAccount(version, snapshotVersion, fromAccountNumber, amountInCents)
        val closedBankAccount = ClosedBankAccount(version, snapshotVersion, fromAccountNumber)
        val event = MoneyWithdrawnEvent(version, snapshotVersion, UUID.randomUUID(), amountInCents, date, name, details, fromAccountNumber, toAccountNumber, type)

        "it should withdraw money from a bank account" {
            event.apply(openBankAccount).balanceInCents shouldBe 0
        }

        "it should not withdraw money from a closed bank account" {
            shouldThrowExactly<IllegalStateException> {
                event.apply(closedBankAccount)
            }
        }

        "it should not withdraw money from an uninitialized account" {
            shouldThrowExactly<IllegalStateException> {
                event.apply(uninitializedBankAccount)
            }
        }
    }

}