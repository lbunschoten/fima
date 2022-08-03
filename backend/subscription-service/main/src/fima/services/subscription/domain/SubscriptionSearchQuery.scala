package fima.services.subscription.domain

import cats.implicits.toBifunctorOps
import fima.services.transaction.TransactionService.SearchTransactionsRequest as SearchTransactionsRequestP
import io.circe.generic.semiauto
import io.circe.parser.parse
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}
import org.postgresql.util.PGobject


case class SubscriptionSearchQuery(filters: Seq[SubscriptionSearchFilters]) {
  def toProto: SearchTransactionsRequestP = SearchTransactionsRequestP().withFilters(filters.map(_.toProto))
}

object SubscriptionSearchQuery {
  implicit val encoder: Encoder[SubscriptionSearchQuery] = semiauto.deriveEncoder
  implicit val decoder: Decoder[SubscriptionSearchQuery] = semiauto.deriveDecoder

  @SuppressWarnings(Array("scalafix:DisableSyntax.throw"))
  val decode: PGobject => SubscriptionSearchQuery = pGobject =>
    parse(pGobject.getValue)
      .leftMap(parsingFailure => throw parsingFailure)
      .merge
      .as[SubscriptionSearchQuery]
      .leftMap(decodingFailure => throw decodingFailure)
      .getOrElse(SubscriptionSearchQuery(Nil))

  val encode: SubscriptionSearchQuery => PGobject = query => {
    val o = new PGobject
    o.setType("json")
    o.setValue(query.asJson.noSpaces)
    o
  }
}

