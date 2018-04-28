package fima.services.transactionstatistics.event

import fima.events.transaction.TransactionAddedEvent
import fima.services.transactionstatistics.repository.StatisticsRepository
import org.apache.kafka.clients.consumer.ConsumerRecord

class ProcessTransactionAddedEvent(private val statisticsRepository: StatisticsRepository) : (ConsumerRecord<Long, TransactionAddedEvent>) -> Unit {

    override fun invoke(record: ConsumerRecord<Long, TransactionAddedEvent>) {
        val transaction = record.value().transaction

        statisticsRepository.insertTransaction(transaction.date.month, transaction.date.year)
    }

}