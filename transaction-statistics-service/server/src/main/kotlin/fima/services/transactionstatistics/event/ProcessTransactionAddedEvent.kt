package fima.services.transactionstatistics.event

import fima.events.transaction.TransactionAddedEvent
import fima.services.transactionstatistics.repository.StatisticsRepository
import org.apache.kafka.clients.consumer.ConsumerRecord

class ProcessTransactionAddedEvent(private val statisticsRepository: StatisticsRepository,
                                   private val checkingAccountNumber: String,
                                   private val savingsAccountNumber: String) : (ConsumerRecord<Long, TransactionAddedEvent>) -> Unit {

  override fun invoke(record: ConsumerRecord<Long, TransactionAddedEvent>) {
    val transaction = record.value().transaction
    val amount =
      when {
        transaction.toAccount == savingsAccountNumber -> 0F
        transaction.fromAccount == savingsAccountNumber -> 0F
        transaction.fromAccount == checkingAccountNumber -> -transaction.amount
        else -> transaction.amount
      }

    statisticsRepository.insertTransaction(transaction.date.month, transaction.date.year, amount.toDouble())
  }

}