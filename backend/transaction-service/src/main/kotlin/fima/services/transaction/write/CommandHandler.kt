package fima.services.transaction.write

import fima.services.transaction.store.EventStore
import fima.services.transaction.write.command.Command
import fima.services.transaction.write.event.Event
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.slf4j.LoggerFactory

class CommandHandler(private val transactionHandler: TransactionHandler,
                     private val jdbi: Jdbi, // TOD() remove
                     private val eventStore: EventStore,
                     private val eventProcessor: EventProcessor,
                     private val eventListeners: Set<(Event) -> Unit>) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun processCommand(aggregateId: String, command: Command): Set<String> {
        return try {
            return transactionHandler.inTransaction {
                logger.info("Process command: ${jdbi.withHandleUnchecked { it.isInTransaction }}")

                val historicEvents = eventStore.readEvents(aggregateId)
                val inputAggregate = eventProcessor.process(historicEvents)

                val newEvents: List<Event> = command.events(aggregateId).mapIndexed { idx, buildEvent ->
                    buildEvent(1 + idx + inputAggregate.version)
                }
                val outputAggregate = newEvents.fold(inputAggregate) { agg, e -> e.apply(agg) }

                val validationErrors = outputAggregate.validate()
                if (validationErrors.isEmpty()) {
                    eventStore.writeEvents(aggregateId, newEvents)
                    newEvents.forEach { event ->
                        eventListeners.forEach { listen -> listen(event) }
                    }
                }

                validationErrors
            }
        } catch (e: Exception) {
            println("""${e.javaClass.canonicalName} ${e.message}""")
            setOf(e.message.orEmpty())
        }
    }

}

interface TransactionHandler {
    fun <T> inTransaction(f: () -> T): T
}

class JdbiTransactionHandler(private val jdbi: Jdbi) : TransactionHandler{
    override fun <T> inTransaction(f: () -> T): T {
        return jdbi.inTransactionUnchecked { f() }
    }
}

class FakeTransactionHandler : TransactionHandler {
    override fun <T> inTransaction(f: () -> T): T = f()
}
