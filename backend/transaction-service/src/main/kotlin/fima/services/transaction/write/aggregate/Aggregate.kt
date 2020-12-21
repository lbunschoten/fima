package fima.services.transaction.write.aggregate

interface Aggregate {
  val version: Int

  fun validate(): Set<String>
}