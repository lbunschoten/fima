package fima.services.transaction.write.command

import fima.services.transaction.write.event.MoneyDepositedEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFieldsExcept
import java.time.LocalDate
import java.util.*

class DepositMoneyCommandSpec : StringSpec() {

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

        val command = DepositMoneyCommand(amountInCents, date, name, details, fromAccountNumber, type)

        "it should generate a MoneyDepositedEvent" {
            command
                .events(toAccountNumber)[0].invoke(version, snapshotVersion)
                .shouldBeEqualToComparingFieldsExcept(
                    MoneyDepositedEvent(
                        version = version,
                        snapshotVersion = snapshotVersion,
                        id = UUID.randomUUID(),
                        amountInCents = amountInCents,
                        date = date,
                        name = name,
                        details = details,
                        fromAccountNumber = fromAccountNumber,
                        toAccountNumber = toAccountNumber,
                        type
                    ),
                    MoneyDepositedEvent::id
                )
        }

    }

}