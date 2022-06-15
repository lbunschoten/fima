package fima.services.subscription.repository

import cats.implicits.{catsSyntaxEq, toBifunctorOps}
import doobie.free.connection.ConnectionIO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import org.postgresql.util.PGobject

import java.util.UUID

sealed trait Recurrence {
  val id: Int
  val name: String
}

object RecurrenceCompanion {

  val values: Seq[Recurrence] = Seq(Monthly, Yearly)

  case object Monthly extends Recurrence {
    override val id: Int = 0
    override val name: String = "monthly"
  }

  case object Yearly extends Recurrence {
    override val id: Int = 1
    override val name: String = "yearly"
  }

  def toEnum(e: Recurrence): String = e.name

  def fromEnum(name: String): Option[Recurrence] = values.find(_.name === name)

}

case class QueryStringFilter(queryString: String)

case class TransactionTagFilter(key: String, value: String)

case class SubscriptionSearchFilters(query: Option[QueryStringFilter], tags: Seq[TransactionTagFilter])

case class SubscriptionSearchQuery(filters: Seq[SubscriptionSearchFilters])

object SubscriptionSearchQuery {
  import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
  implicit val queryStringFilterDecoder: Decoder[QueryStringFilter] = deriveDecoder[QueryStringFilter]
  implicit val transactionTagFilterDecoder: Decoder[TransactionTagFilter] = deriveDecoder[TransactionTagFilter]
  implicit val subscriptionSearchFiltersDecoder: Decoder[SubscriptionSearchFilters] = deriveDecoder[SubscriptionSearchFilters]
  implicit val subscriptionSearchQueryDecoder: Decoder[SubscriptionSearchQuery] = deriveDecoder[SubscriptionSearchQuery]

  implicit val queryStringFilterEncoder: Encoder[QueryStringFilter] = deriveEncoder[QueryStringFilter]
  implicit val transactionTagFilterEncoder: Encoder[TransactionTagFilter] = deriveEncoder[TransactionTagFilter]
  implicit val subscriptionSearchFiltersEncoder: Encoder[SubscriptionSearchFilters] = deriveEncoder[SubscriptionSearchFilters]
  implicit val subscriptionSearchQueryEncoder: Encoder[SubscriptionSearchQuery] = deriveEncoder[SubscriptionSearchQuery]

  val empty: SubscriptionSearchQuery = SubscriptionSearchQuery(Seq.empty)

  @SuppressWarnings(Array("scalafix:DisableSyntax.throw"))
  val decode: PGobject => SubscriptionSearchQuery = pGobject =>
    parse(pGobject.getValue)
      .leftMap(parsingFailure => throw parsingFailure)
      .merge
      .as[SubscriptionSearchQuery]
      .leftMap(decodingFailure => throw decodingFailure)
      .getOrElse(empty)

  val encode: SubscriptionSearchQuery => PGobject = query => {
    val o = new PGobject
    o.setType("json")
    o.setValue(query.asJson.noSpaces)
    o
  }
}

case class Subscription(id: UUID, name: String, query: SubscriptionSearchQuery, recurrence: Recurrence)

trait SubscriptionRepository {
  def insert(subscription: Subscription): ConnectionIO[Int]
  def findById(id: UUID): ConnectionIO[Option[Subscription]]
  def findAll(): ConnectionIO[List[Subscription]]
}
