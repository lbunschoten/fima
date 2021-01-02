package fima.services.transaction.write.event

import fima.services.transaction.write.aggregate.BankAccount
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
abstract class Event : EventVersion {

    abstract val version: Int

    @Suppress("unused")
    val at: Long = Instant.now().toEpochMilli()


    abstract fun apply(aggregate: BankAccount): BankAccount
}

interface EventVersion {
    val eventVersion: Int
}

interface EventVersion1 : EventVersion {
    override val eventVersion: Int get() = 1
}
