package fima.services.subscription.repository

import cats.implicits.{catsSyntaxEq, toBifunctorOps}
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.log.LogHandler
import org.postgresql.util.PGobject

import java.util.UUID

class PostgresSubscriptionRepository extends SubscriptionRepository {

  private implicit val logHandler: LogHandler = LogHandler(println)
  private implicit val RecurrenceMeta: Meta[Recurrence] = pgEnumStringOpt("recurrence", RecurrenceCompanion.fromEnum, RecurrenceCompanion.toEnum)
  private implicit val subscriptionSearchQueryMeta: Meta[SubscriptionSearchQuery] =
    Meta.Advanced
      .other[PGobject]("json")
      .timap[SubscriptionSearchQuery](SubscriptionSearchQuery.decode)(SubscriptionSearchQuery.encode)

  def insert(subscription: Subscription): ConnectionIO[Int] = {
    sql"insert into transaction.subscription (id, name, query, recurrence) values (${subscription.id}, ${subscription.name}, ${subscription.query}, ${subscription.recurrence})"
      .update
      .run
  }

  def findById(id: UUID): ConnectionIO[Option[Subscription]] = {
    sql"SELECT id, name, query, recurrence from transaction.subscription WHERE id = $id"
      .query[Subscription]
      .option
  }

  def findAll(): ConnectionIO[List[Subscription]] = {
    sql"SELECT id, name, query, recurrence from transaction.subscription"
      .query[Subscription]
      .to[List]
  }


}
