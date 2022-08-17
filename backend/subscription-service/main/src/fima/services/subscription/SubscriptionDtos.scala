package fima.services.subscription

import fima.domain.transaction.TransactionDomain.Transaction
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

object SubscriptionDtos {

  case class SubscriptionDto(id: String, name: String, recurrence: String)

  case class TransactionDto(
    id: UUID,
    date: String,
    `type`: String,
    name: String,
    toAccount: String,
    fromAccount: String,
    amount: Float,
    tags: Map[String, String]
  )

  object TransactionDto {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    def fromDomain(t: Transaction): TransactionDto = {
      val date = t.date.map { date => LocalDate.of(date.year, date.month, date.day).format(dateFormatter) }.getOrElse("")
      TransactionDto(UUID.fromString(t.id), date, t.`type`.name, t.name, t.toAccount, t.fromAccount, t.amount, t.tags)
    }
  }

  case class GetSubscriptionResponseDto(
    subscription: Option[SubscriptionDto],
    transactions: Seq[TransactionDto]
  )

  implicit val subscriptionDtoCodec: Codec[SubscriptionDto] = deriveCodec
  implicit val transactionDtoCodec: Codec[TransactionDto] = deriveCodec
  implicit val getSubscriptionResponseDtoCodec: Codec[GetSubscriptionResponseDto] = deriveCodec

}
