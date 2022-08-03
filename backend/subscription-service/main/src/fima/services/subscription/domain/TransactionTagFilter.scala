package fima.services.subscription.domain

import fima.services.transaction.TransactionService.TransactionTagFilter as TransactionTagFilterP
import io.circe.Codec
import io.circe.generic.semiauto

case class TransactionTagFilter(key: String, value: String) {
  def toProto: TransactionTagFilterP = TransactionTagFilterP(key, value)
}

object TransactionTagFilter {
  implicit val codec: Codec[TransactionTagFilter] = semiauto.deriveCodec
}