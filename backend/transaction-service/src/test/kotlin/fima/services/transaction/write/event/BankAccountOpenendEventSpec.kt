package fima.services.transaction.write.event

import fima.services.transaction.write.aggregate.OpenBankAccount
import fima.services.transaction.write.aggregate.UniniatilizedAccount
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.types.beOfType

class BankAccountOpenendEventSpec : StringSpec() {

    init {
        val version = 0
        val snapshotVersion = 0
        val accountNumber = "account number"

        val aggregate = UniniatilizedAccount(accountNumber)
        val event = BankAccountOpenedEvent(version, snapshotVersion, accountNumber)

        "it should open a bank account" {
            event.apply(aggregate) should beOfType<OpenBankAccount>()
        }

    }

}