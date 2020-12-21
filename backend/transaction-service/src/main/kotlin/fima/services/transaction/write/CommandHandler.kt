package fima.services.transaction.write

import fima.services.transaction.write.command.Command
import fima.services.transaction.write.event.Event
import fima.services.transaction.write.store.EventStore

class CommandHandler(private val eventStore: EventStore,
                     private val eventProcessor: EventProcessor,
                     private val eventListeners: Set<(Event) -> Unit>) {

  fun processCommand(aggregateId: String, command: Command): Set<String> {
    try {
      val historicEvents = eventStore.readEvents(aggregateId)
      val inputAggregate = eventProcessor.process(historicEvents)

      val newEvents: List<Event> = command.events(aggregateId).mapIndexed { idx, buildEvent ->
        buildEvent(1 + idx + inputAggregate.version)
      }
      val outputAggregate = newEvents.fold(inputAggregate, { agg, e -> e.apply(agg) })

      val validationErrors = outputAggregate.validate()
      if (validationErrors.isEmpty()) {
        eventStore.writeEvents(aggregateId, newEvents)
        newEvents.forEach { event ->
          eventListeners.forEach { listen -> listen(event) }
        }
      }

      return validationErrors
    } catch (e: Exception) {
      println("""${e.javaClass.canonicalName} ${e.message}""")
      return setOf(e.message.orEmpty())
    }
  }

}
