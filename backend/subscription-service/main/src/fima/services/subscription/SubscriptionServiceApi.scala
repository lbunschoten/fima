package fima.services.subscription

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{ContentTypeRange, ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.implicits.*
import doobie.Transactor
import doobie.implicits.*
import io.circe.generic.semiauto.*
import CirceSupport.*
import com.google.protobuf.GeneratedMessage
import fima.domain.transaction.TransactionDomain.Transaction
import fima.services.subscription.repository.{Subscription, SubscriptionRepository, SubscriptionSearchQuery}
import fima.services.transaction.TransactionService.{QueryStringFilter, SearchFilter, SearchTransactionsRequest, SearchTransactionsResponse, TransactionTagFilter}
import fima.services.transaction.TransactionService.TransactionServiceGrpc.{TransactionService, TransactionServiceStub}
import io.circe.syntax.*
import io.circe.{Codec, Decoder, Encoder}
import io.grpc.Metadata

import java.time.{Instant, LocalDate}
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import fima.services.subscription.implicits.{asSearchRequest, toIO}

import java.time.format.DateTimeFormatter
import scala.concurrent.duration.DurationInt


class SubscriptionServiceApi(
  private val subscriptionRepository: SubscriptionRepository,
  private val transactionService: TransactionServiceStub,
  transactor: Transactor[IO]
)(
  private implicit val ec: ExecutionContext,
  private implicit val iORuntime: IORuntime
) extends CORSHandler {

  import CirceSupport._

  private implicit val subscriptionDtoEncoder: Encoder[SubscriptionDto] = deriveEncoder
  private implicit val transactionDtoEncoder: Encoder[TransactionDto] = deriveEncoder
  private implicit val getSubscriptionResponseDtoEncoder: Encoder[GetSubscriptionResponseDto] = deriveEncoder
  private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

  val routes: Route =
    concat(
      path("subscription" / Remaining)(getSubscriptionById),
      path("subscriptions")(getSubscriptions)
    )

  private def getSubscriptionById(id: String): Route = {
    corsHandler(
      get {
        try {
          val response = for {
            subscription: Option[Subscription] <- subscriptionRepository.findById(UUID.fromString(id)).transact(transactor)
            searchTransactionsResponse <- subscription.map { s =>
              println(s)
              val a: IO[Future[Seq[Transaction]]] = IO(transactionService.searchTransactions(s.query.asSearchRequest).map(_.transactions)).timeoutTo(2.seconds, IO(Seq.empty))
              a.map(println)
              a
            }.getOrElse(IO(Seq.empty))
          } yield {
            println("SUCCESS")
            GetSubscriptionResponseDto(
              subscription = subscription.map { s => SubscriptionDto(s.id.toString, s.name, s.recurrence.name.toUpperCase()) },
              transactions = searchTransactionsResponse.map { (t: Transaction) =>
                val date = t.date.map { date => LocalDate.of(date.year, date.month, date.day).format(dateFormatter) }.getOrElse("")
                TransactionDto(UUID.fromString(t.id), date, t.`type`.name, t.name, t.toAccount, t.fromAccount, t.amount, t.tags)
              }
            )
          }

          Directives.onSuccess(response.unsafeToFuture())(i => complete(i))
        } catch {
          case NonFatal(e) =>
            println(e)
            failWith(e)
          case e: Throwable =>
            println(e)
            failWith(e)
        }
      }
    )
  }

  private def getSubscriptions: Route =
    corsHandler(
      get {
        val subscriptions: Future[Seq[SubscriptionDto]] = subscriptionRepository
          .findAll()
          .transact(transactor)
          .unsafeToFuture()
          .map(_.map { s => SubscriptionDto(s.id.toString, s.name, s.recurrence.name.toUpperCase()) }.toSeq)
        Directives.onSuccess(subscriptions)(i => complete(i))
      }
    )
}

object CirceSupport {
  private def jsonContentTypes: List[ContentTypeRange] =
    List(ContentTypes.`application/json`)

  implicit final def unmarshaller[A: Decoder]: FromEntityUnmarshaller[A] = {
    Unmarshaller.stringUnmarshaller
      .forContentTypes(jsonContentTypes: _*)
      .flatMap { _ => _ => json => io.circe.parser.decode(json).fold(Future.failed, Future.successful) }
  }

  implicit final def marshaller[A: Encoder]: ToEntityMarshaller[A] = {
    Marshaller.withFixedContentType(ContentTypes.`application/json`) { a =>
      HttpEntity(ContentTypes.`application/json`, a.asJson.noSpaces)
    }
  }
}

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

case class GetSubscriptionResponseDto(
  subscription: Option[SubscriptionDto],
  transactions: Seq[TransactionDto]
)

object implicits {
  extension (subscriptionSearchQuery: SubscriptionSearchQuery) {
    def asSearchRequest: SearchTransactionsRequest = {
      SearchTransactionsRequest()
        .withFilters(
          subscriptionSearchQuery.filters.map { f =>
            SearchFilter(
              f.query.map { q => QueryStringFilter(q.queryString) },
              f.tags.map { t => TransactionTagFilter(t.key, t.value) }
            )
          }
        )
    }
  }

  extension [T] (f: Future[T]) {
    def toIO: IO[T] = IO.fromFuture(IO { f })
  }
}
