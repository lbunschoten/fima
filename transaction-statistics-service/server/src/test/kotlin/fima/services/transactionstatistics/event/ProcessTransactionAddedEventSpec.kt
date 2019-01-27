package fima.services.transactionstatistics.event

import fima.domain.transaction.Date
import fima.domain.transaction.Transaction
import fima.events.transaction.TransactionAddedEvent
import fima.services.transactionstatistics.repository.StatisticsRepository
import io.kotlintest.specs.StringSpec
import io.mockk.mockk
import io.mockk.verify
import org.apache.kafka.clients.consumer.ConsumerRecord

class ProcessTransactionAddedEventSpec : StringSpec() {

  override fun isInstancePerTest() = true

  init {
    val repository = mockk<StatisticsRepository>(relaxed = true)
    val checkingAccountNumber = "CheckingAccountNumber"
    val savingsAccountNumber = "SavingsAccountNumber"
    val event = ProcessTransactionAddedEvent(repository, checkingAccountNumber, savingsAccountNumber)

    "it should insert a transaction to the statistics repository" {
      event.invoke(ConsumerRecord("topic", 1, 0, 1L, transactionAddedEvent()))
      verify { repository.insertTransaction(1, 2018, 200.toDouble()) }
    }
  }

  private fun transactionAddedEvent(): TransactionAddedEvent {
    return TransactionAddedEvent
      .newBuilder()
      .setTransaction(
        Transaction
          .newBuilder()
          .setFromAccount("SomeElsesAccount")
          .setAmount(200.toFloat())
          .setDate(
            Date
              .newBuilder()
              .setMonth(1)
              .setYear(2018)
              .build()
          ).build()
      ).build()
  }

}