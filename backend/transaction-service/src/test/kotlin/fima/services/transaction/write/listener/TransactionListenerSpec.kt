package fima.services.transaction.write.listener

import fima.services.transaction.write.TransactionService
import fima.services.transaction.write.event.BankAccountClosedEvent
import fima.services.transaction.write.event.MoneyDepositedEvent
import fima.services.transaction.write.event.MoneyWithdrawnEvent
import io.kotest.core.spec.style.StringSpec
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import java.util.*

class TransactionListenerSpec : StringSpec() {

    init {
        val transactionService = mockk<TransactionService>(relaxUnitFun = true)
        val listener = TransactionListener(transactionService)
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
            verify { transactionService wasNot called }
        }

        "it should store a transaction for a MoneyWithdrawnEvent" {
            listener.invoke(moneyWithdrawnEvent)
            verify { transactionService.insertTransaction(
                id = moneyWithdrawnEvent.id,
                date = date,
                name = name,
                fromAccountNumber = fromAccountNumber,
                toAccountNumber = toAccountNumber,
                type = type,
                amountInCents = balanceInCents
            ) }
        }

        "it should store a transaction for a MoneyDepositedEvent" {
            listener.invoke(moneyDepositedEvent)
            verify { transactionService.insertTransaction(
                id = moneyDepositedEvent.id,
                date = date,
                name = name,
                fromAccountNumber = fromAccountNumber,
                toAccountNumber = toAccountNumber,
                type = type,
                amountInCents = balanceInCents
            ) }
        }

    }

}