package fima.services.transaction.write

import fima.services.transaction.write.aggregate.BankAccount
import fima.services.transaction.write.event.BankAccountOpenedEvent
import fima.services.transaction.write.event.Event
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class EventProcessorSpec : StringSpec() {

    class IncreaseBalanceEvent(private val newBalance: Long) : Event() {
        override val eventVersion: Int = 0
        override val version: Int = 0
        override val snapshotVersion: Int = 0

        override fun apply(aggregate: BankAccount): BankAccount {
            return aggregate.withBalance(newBalance)
        }
    }

    init {
        val eventProcessor = EventProcessor()

        "it should apply all events on an aggregate in order" {
            val updatedAggregate = eventProcessor.process("account number", listOf(
                BankAccountOpenedEvent(version = 1, snapshotVersion = 1, accountNumber = "account number"),
                IncreaseBalanceEvent(1),
                IncreaseBalanceEvent(2)
            ))

            updatedAggregate.balanceInCents shouldBe 2
        }
    }

}