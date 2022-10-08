package fima.services.transaction.write.command

import fima.services.transaction.write.event.BankAccountOpenedEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class OpenBankAccountCommandSpec : StringSpec() {

    init {
        val version = 0
        val snapshotVersion = 0
        val accountNumber = "account number"
        val command = OpenBankAccountCommand(accountNumber)

        "it should generate a BankAccountOpenedEvent" {
            command.events(accountNumber)[0].invoke(version, snapshotVersion) shouldBe BankAccountOpenedEvent(
                version = version,
                snapshotVersion = snapshotVersion,
                accountNumber = accountNumber
            )
        }

    }

}