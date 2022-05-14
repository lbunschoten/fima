package fima.services.transaction.write.aggregate

import fima.services.transaction.write.event.Event

interface Aggregate {
    val version: Int
    val snapshotVersion: Int

    fun validate(): Set<String>
    fun snapshot(): Event
}