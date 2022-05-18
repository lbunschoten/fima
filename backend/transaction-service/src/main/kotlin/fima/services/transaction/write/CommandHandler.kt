package fima.services.transaction.write

import fima.services.transaction.store.EventStore
import fima.services.transaction.write.command.Command
import fima.services.transaction.write.event.Event
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked

class CommandHandler(private val transactionHandler: TransactionHandler,
                     private val eventStore: EventStore,
                     private val eventProcessor: EventProcessor,
                     private val eventListeners: Set<(Event) -> Unit>) {

    fun processCommand(aggregateId: String, command: Command): Set<String> {
        return try {
            transactionHandler.inTransaction {
                val historicEvents = eventStore.readLatestEvents(aggregateId)
                val inputAggregate = eventProcessor.process(aggregateId, historicEvents)

                val newEvents: List<Event> = command.events(aggregateId).mapIndexed { idx, buildEvent ->
                    buildEvent(1 + idx + inputAggregate.version, inputAggregate.snapshotVersion)
                }
                val outputAggregate = newEvents.fold(inputAggregate) { agg, e -> e.apply(agg) }

                val validationErrors = outputAggregate.validate()
                if (validationErrors.isEmpty()) {
                    eventStore.writeEvents(aggregateId, newEvents)
                    newEvents.forEach { event ->
                        eventListeners.forEach { listen -> listen(event) }
                    }

                    if (shouldCreateSnapshot(historicEvents + newEvents)) {
                        eventStore.writeEvents(aggregateId, listOf(outputAggregate.snapshot()))
                    }
                }

                validationErrors
            }
        } catch (e: Exception) {
            println("""${e.javaClass.canonicalName} ${e.message}""")
            setOf(e.message.orEmpty())
        }
    }

    private fun shouldCreateSnapshot(eventsInCurrentSnapshot: List<Event>) = eventsInCurrentSnapshot.size >= 10
}

interface TransactionHandler {
    fun <T> inTransaction(f: () -> T): T
}

class JdbiTransactionHandler(private val jdbi: Jdbi) : TransactionHandler{
    override fun <T> inTransaction(f: () -> T): T {
        return jdbi.inTransactionUnchecked { f() }
    }
}
