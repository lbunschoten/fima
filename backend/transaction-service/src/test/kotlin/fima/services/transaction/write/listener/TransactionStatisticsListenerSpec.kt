package fima.services.transaction.write.listener

import fima.services.transaction.write.TransactionStatisticsService
import fima.services.transaction.write.event.BankAccountClosedEvent
import fima.services.transaction.write.event.MoneyDepositedEvent
import fima.services.transaction.write.event.MoneyWithdrawnEvent
import io.kotest.core.spec.style.StringSpec
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import java.util.*

class TransactionStatisticsListenerSpec : StringSpec() {

    init {
        val transactionStatisticsService = mockk<TransactionStatisticsService>(relaxUnitFun = true)
        val listener = TransactionStatisticsListener(transactionStatisticsService)
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
            verify { transactionStatisticsService wasNot called }
        }

        "it should add statistics for a MoneyWithdrawnEvent" {
            listener.invoke(moneyWithdrawnEvent)
            verify { transactionStatisticsService.addTransaction(date, -balanceInCents) }
        }

        "it should add statistics for a MoneyDepositedEvent" {
            listener.invoke(moneyDepositedEvent)
            verify { transactionStatisticsService.addTransaction(date, balanceInCents) }
        }

    }

}