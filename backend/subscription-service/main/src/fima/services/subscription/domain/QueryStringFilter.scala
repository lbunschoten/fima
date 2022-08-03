package fima.services.subscription.domain

import fima.services.transaction.TransactionService.QueryStringFilter as QueryStringFilterP
import io.circe.Codec
import io.circe.generic.semiauto

case class QueryStringFilter(queryString: String) {
  def toProto: QueryStringFilterP = QueryStringFilterP(queryString)
}

object QueryStringFilter {
  implicit val codec: Codec[QueryStringFilter] = semiauto.deriveCodec
}