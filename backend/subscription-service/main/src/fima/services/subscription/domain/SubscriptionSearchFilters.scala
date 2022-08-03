package fima.services.subscription.domain

import fima.services.transaction.TransactionService.SearchFilter as SearchFilterP
import io.circe.Codec
import io.circe.generic.semiauto

case class SubscriptionSearchFilters(query: Option[QueryStringFilter], tags: Seq[TransactionTagFilter]) {
  def toProto: SearchFilterP = SearchFilterP().update(
    _.optionalQuery := query.map(_.toProto),
    _.tags := tags.map(_.toProto)
  )
}

object SubscriptionSearchFilters {
  implicit val codec: Codec[SubscriptionSearchFilters] = semiauto.deriveCodec
}

