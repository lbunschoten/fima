package fima.services.transaction.write.command

import fima.services.transaction.write.event.MoneyDepositedEvent
import fima.services.transaction.write.event.MoneyWithdrawnEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import java.time.LocalDate
import java.util.*

class WithdrawMoneyCommandSpec : StringSpec() {

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

        val command = WithdrawMoneyCommand(amountInCents, date, name, details, toAccountNumber, type)

        "it should generate a MoneyWithdrawnEvent" {
            command
                .events(fromAccountNumber)[0].invoke(version, snapshotVersion)
                .shouldBeEqualToIgnoringFields(
                    MoneyWithdrawnEvent(
                        version = version,
                        snapshotVersion = snapshotVersion,
                        id = UUID.randomUUID(),
                        amountInCents = amountInCents,
                        date = date,
                        name = name,
                        details = details,
                        fromAccountNumber = fromAccountNumber,
                        toAccountNumber = toAccountNumber,
                        type = type
                    ),
                    MoneyWithdrawnEvent::id,
                    MoneyWithdrawnEvent::at,
                )
        }

    }

}