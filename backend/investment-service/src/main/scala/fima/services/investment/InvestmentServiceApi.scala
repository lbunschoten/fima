package fima.services.investment

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
import fima.services.investment.domain.{MarketIndex, Position, SectorType, Stock, Transaction}
import fima.services.investment.domain.StockSymbol
import fima.services.investment.repository.{StockRepository, TransactionRepository}
import io.circe.generic.semiauto.*
import CirceSupport._
import io.circe.syntax.*
import io.circe.{Codec, Decoder, Encoder}

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class InvestmentServiceApi(
  private val stockRepository: StockRepository,
  private val transactionRepository: TransactionRepository,
  transactor: Transactor[IO]
)(
  private implicit val ec: ExecutionContext,
  private implicit val iORuntime: IORuntime
) extends CORSHandler {

  import CirceSupport._
  implicit val modeCodec: Codec[MarketIndex] = deriveCodec[MarketIndex]
  implicit val stockEncoder: Encoder[Stock] = deriveEncoder
  implicit val positionEncoder: Encoder[Position] = deriveEncoder
  implicit val transactionEncoder: Encoder[Transaction] = deriveEncoder
  implicit val sectorEncoder: Encoder[SectorDto] = deriveEncoder
  implicit val marketEncoder: Encoder[MarketDto] = deriveEncoder
  implicit val investmentEncoder: Encoder[InvestmentMethodDto] = deriveEncoder

  val routes: Route =
    concat(
      path("sectors")(getSectors),
      path("markets")(getMarkets),
      path("methods")(getMethods),
      path("transactions")(getTransactions),
      path("stocks")(getStocks),
      path("stock" / Remaining)(getStockBySymbol)
    )

  private def getStocksFromDb: IO[List[Stock]] = {
    stockRepository.findAll().transact(transactor)
  }

  private def getTransactionsFromDb(symbol: StockSymbol): IO[Int] = {
    transactionRepository
      .findBySymbol(symbol)
      .transact(transactor)
      .map { transactions => transactions.map(_.shares).sum }
  }

  private def getStocks: Route =
    corsHandler(
      get {
        try {
          val stocks: Future[Seq[Position]] = getStocksFromDb
            .flatMap { stocks =>
              stocks.map { stock =>
                getTransactionsFromDb(stock.symbol)
                  .map { shares => Position(stock, shares) }
              }.sequence
            }.unsafeToFuture()
            .map(_.toSeq)

          Directives.onSuccess(stocks)(i => complete(i))
        } catch {
          case NonFatal(e) =>
            println(e)
            failWith(e)
        }

      }
    )

  private def getStockBySymbol(symbol: StockSymbol): Route = {
    corsHandler(
      get {
        val stocks = stockRepository
          .findBySymbol(symbol)
          .transact(transactor)
          .unsafeToFuture()

        Directives.onSuccess(stocks)(i => complete(i))
      }
    )
  }

  private def getMarkets: Route = {
    corsHandler(
      get {
        complete(MarketIndex.values.map(s => MarketDto(s.value, s)))
      }
    )
  }

  private def getSectors: Route = {
    corsHandler(
      get {
        complete(SectorType.values.map(s => SectorDto(s.name, s)))
      }
    )
  }

  private def getMethods: Route = {
    corsHandler(
      get {
        complete(domain.InvestmentMethod.values.map(s => InvestmentMethodDto(s.name, s)))
      }
    )
  }

  private def getTransactions: Route = {
    corsHandler(
      get {
        val transactions: Future[Seq[Transaction]] = transactionRepository
          .findAll()
          .transact(transactor)
          .unsafeToFuture()
          .map(_.toSeq)

        Directives.onSuccess(transactions)(i => complete(i))
      }
    )
  }

  case class MarketDto(name: String, `type`: MarketIndex)

  case class SectorDto(name: String, `type`: SectorType)

  case class InvestmentMethodDto(name: String, `type`: domain.InvestmentMethod)

  case class StockDto(
    symbol: StockSymbol,
    name: String,
    index: MarketDto,
    sector: SectorDto,
    investmentType: InvestmentMethodDto,
    marketValue: BigDecimal,
    updatedAt: Instant
  )

}

object CirceSupport {
  private def jsonContentTypes: List[ContentTypeRange] =
    List(ContentTypes.`application/json`)

  implicit final def unmarshaller[A: Decoder]: FromEntityUnmarshaller[A] = {
    Unmarshaller.stringUnmarshaller
      .forContentTypes(jsonContentTypes: _*)
      .flatMap { ctx => mat => json =>
        io.circe.parser.decode(json).fold(Future.failed, Future.successful)
      }
  }

  implicit final def marshaller[A: Encoder]: ToEntityMarshaller[A] = {
    Marshaller.withFixedContentType(ContentTypes.`application/json`) { a =>
      HttpEntity(ContentTypes.`application/json`, a.asJson.noSpaces)
    }
  }
}