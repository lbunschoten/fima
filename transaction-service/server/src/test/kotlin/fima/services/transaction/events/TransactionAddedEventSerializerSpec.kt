package fima.services.transaction.events

import fima.events.transaction.TransactionAddedEvent
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TransactionAddedEventSerializerSpec : StringSpec() {

  override fun isInstancePerTest(): Boolean = true

  init {
    val serializer = TransactionAddedEventSerializer()

    "it should serialize the TransactionAddedEvent" {
      val event = TransactionAddedEvent.getDefaultInstance()
      serializer.serialize("topic", event) shouldBe event.toByteArray()
    }

  }
}