package fima.services.transaction.write.listener

import fima.services.transaction.write.TaggingService
import fima.services.transaction.write.event.BankAccountClosedEvent
import fima.services.transaction.write.event.MoneyDepositedEvent
import fima.services.transaction.write.event.MoneyWithdrawnEvent
import io.kotest.core.spec.style.StringSpec
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import java.util.*

class TransactionTaggingListenerSpec : StringSpec() {

    init {
        val taggingService = mockk<TaggingService>(relaxUnitFun = true)
        val listener = TransactionTaggingListener(taggingService)
        val version = 0
        val snapshotVersion = 0
        val balanceInCents = 100L
        val date = LocalDate.now()
        val name = "name"
        val details = "details"
        val fromAccountNumber = "from account number"
        val toAccountNumber = "to account number"
        val type = "type"

        val moneyWithdrawnEvent = MoneyWithdrawnEvent(version, snapshotVersion, UUID.randomUUID(), balanceInCents, date, name, details, fromAccountNumber, toAccountNumber, type)
        val moneyDepositedEvent = MoneyDepositedEvent(version, snapshotVersion, UUID.randomUUID(), balanceInCents, date, name, details, fromAccountNumber, toAccountNumber, type)
        val irrelevantEvent = BankAccountClosedEvent(version, snapshotVersion)

        "it should ignore irrelevant events" {
            listener.invoke(irrelevantEvent)
            verify { taggingService wasNot called }
        }

        "it should add tags for a MoneyWithdrawnEvent" {
            listener.invoke(moneyWithdrawnEvent)
            verify { taggingService.tagTransaction(moneyWithdrawnEvent) }
        }

        "it should add tags for a MoneyDepositedEvent" {
            listener.invoke(moneyDepositedEvent)
            verify { taggingService.tagTransaction(moneyDepositedEvent) }
        }

    }

}