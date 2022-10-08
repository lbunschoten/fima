package fima.services.transaction.write.command

import fima.services.transaction.write.event.BankAccountClosedEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CloseBankAccountCommandSpec : StringSpec() {

    init {
        val version = 0
        val snapshotVersion = 0
        val accountNumber = "from account number"
        val command = CloseBankAccountCommand()

        "it should generate a BankAccountClosedEvent" {
            command.events(accountNumber)[0].invoke(version, snapshotVersion) shouldBe BankAccountClosedEvent(
                version = version,
                snapshotVersion = snapshotVersion
            )
        }

    }

}