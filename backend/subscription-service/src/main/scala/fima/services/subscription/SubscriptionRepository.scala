package fima.services.subscription

import doobie.implicits.toSqlInterpolator
import doobie.postgres.implicits._
import doobie.util.log.LogHandler
import doobie.{ConnectionIO, Meta}

import java.util.UUID


sealed trait Recurrence {
  val id: Int
  val name: String
}

object Recurrence {

  val values = Seq(Monthly, Yearly)

  case object Monthly extends Recurrence {
    override val id: Int = 0
    override val name: String = "monthly"
  }

  case object Yearly extends Recurrence {
    override val id: Int = 1
    override val name: String = "yearly"
  }

  def toEnum(e: Recurrence): String = e.name
  def fromEnum(name: String): Option[Recurrence] = values.find(_.name == name)

}


case class Subscription(id: UUID, name: String, recurrence: Recurrence)

class SubscriptionRepository {

  private implicit val logHandler: LogHandler = LogHandler(println)
  private implicit val RecurrenceMeta: Meta[Recurrence] = pgEnumStringOpt("recurrence", Recurrence.fromEnum, Recurrence.toEnum)

  def insert(subscription: Subscription): ConnectionIO[Int] = {
    sql"insert into transaction.subscription (id, name, recurrence) values (${subscription.id}, ${subscription.name}, ${subscription.recurrence})"
      .update
      .run
  }

  def findById(id: UUID): ConnectionIO[Option[Subscription]] = {
    sql"SELECT (id, name, recurrence) from transaction.subscription WHERE id = $id"
      .query[Subscription]
      .option
  }

  def findAll(): ConnectionIO[List[Subscription]] = {
    sql"SELECT (id, name, recurrence) from transaction.subscription"
      .query[Subscription]
      .to[List]
  }


}
