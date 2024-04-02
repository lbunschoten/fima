package fima.services.subscription.repository

import cats.effect.IO
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.log.{LogEvent, LogHandler}
import doobie.util.transactor.Transactor
import fima.services.subscription.domain.{Recurrence, Subscription, SubscriptionSearchQuery}
import org.postgresql.util.PGobject
import zio.*
import zio.interop.catz.*

import java.util.UUID

object PostgresSubscriptionRepository {

  lazy val live: ZLayer[Transactor[Task], Nothing, PostgresSubscriptionRepository] =
    ZLayer.fromFunction { (t: Transactor[Task]) => new PostgresSubscriptionRepository(t) }
}

class PostgresSubscriptionRepository(transactor: Transactor[Task]) {
  private implicit val logHandler: LogHandler[IO] = (logEvent: LogEvent) => IO {
    println(logEvent.sql)
  }
  private implicit val recurrenceMeta: Meta[Recurrence] = pgEnumStringOpt("recurrence", Recurrence.fromEnum, Recurrence.toEnum)
  private implicit val subscriptionSearchQueryMeta: Meta[SubscriptionSearchQuery] =
    Meta.Advanced
      .other[PGobject]("json")
      .timap[SubscriptionSearchQuery](SubscriptionSearchQuery.decode)(SubscriptionSearchQuery.encode)

  def findById(id: UUID): Task[Option[Subscription]] = {
    sql"SELECT id, name, query, recurrence from transaction.subscription WHERE id = $id"
      .query[Subscription]
      .option
      .transact(transactor)
  }

  def findAll(): Task[List[Subscription]] = {
    sql"SELECT id, name, query, recurrence from transaction.subscription"
      .query[Subscription]
      .to[List]
      .transact(transactor)
  }
}
