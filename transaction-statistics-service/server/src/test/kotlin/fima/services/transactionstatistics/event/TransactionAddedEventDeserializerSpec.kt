package fima.services.transactionstatistics.event

import fima.events.transaction.TransactionAddedEvent
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TransactionAddedEventDeserializerSpec : StringSpec() {

  override fun isInstancePerTest(): Boolean = true

  init {
    val transactionAddedEventDeserializer = TransactionAddedEventDeserializer()

    "it should deserialize the TransactionAddedEvent" {
      val event = TransactionAddedEvent.getDefaultInstance()
      transactionAddedEventDeserializer.deserialize("topic", event.toByteArray()) shouldBe event
    }

  }

}