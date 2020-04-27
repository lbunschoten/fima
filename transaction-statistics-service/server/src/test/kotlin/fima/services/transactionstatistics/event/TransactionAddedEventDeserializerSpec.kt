package fima.services.transactionstatistics.event

import fima.events.transaction.TransactionAddedEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TransactionAddedEventDeserializerSpec : StringSpec() {

  init {
    val transactionAddedEventDeserializer = TransactionAddedEventDeserializer()

    "it should deserialize the TransactionAddedEvent" {
      val event = TransactionAddedEvent.getDefaultInstance()
      transactionAddedEventDeserializer.deserialize("topic", event.toByteArray()) shouldBe event
    }

  }

}