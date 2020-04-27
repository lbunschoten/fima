package fima.services.transaction.events

import fima.events.transaction.TransactionAddedEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TransactionAddedEventSerializerSpec : StringSpec() {

  init {
    val serializer = TransactionAddedEventSerializer()

    "it should serialize the TransactionAddedEvent" {
      val event = TransactionAddedEvent.getDefaultInstance()
      serializer.serialize("topic", event) shouldBe event.toByteArray()
    }

  }
}