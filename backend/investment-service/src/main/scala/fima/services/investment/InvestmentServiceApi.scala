package fima.services.investment

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import cats.effect.{IO, Resource}
import cats.implicits._
import doobie.Transactor
import doobie.implicits._
import fima.services.investment.model.Stock.StockSymbol
import fima.services.investment.model._
import fima.services.investment.repository.{StockRepository, TransactionRepository}
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveEnumerationCodec
import io.circe.{Codec, Encoder}

import scala.concurrent.{ExecutionContext, Future}

class InvestmentServiceApi(
  private val stockRepository: StockRepository,
  private val transactionRepository: TransactionRepository,
  transactor: Resource[IO, Transactor[IO]]
)(private implicit val ec: ExecutionContext) extends CORSHandler {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.extras.semiauto.deriveConfiguredEncoder

  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val modeCodec: Codec[MarketIndex] = deriveEnumerationCodec[MarketIndex]
  implicit val stockEncoder: Encoder[Stock] = deriveConfiguredEncoder
  implicit val positionEncoder: Encoder[Position] = deriveConfiguredEncoder
  implicit val transactionEncoder: Encoder[Transaction] = deriveConfiguredEncoder
  implicit val sectorEncoder: Encoder[Sector] = deriveConfiguredEncoder
  implicit val marketEncoder: Encoder[Market] = deriveConfiguredEncoder
  implicit val investmentEncoder: Encoder[InvestmentMethod] = deriveConfiguredEncoder

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
    transactor
      .use { xa => stockRepository.findAll().transact(xa) }
  }

  private def getTransactionsFromDb(symbol: StockSymbol): IO[Int] = {
    transactor
      .use { xa => transactionRepository.findBySymbol(symbol).transact(xa) }
      .map { transactions => transactions.map(_.shares).sum }
  }

  private def getStocks: Route =
    corsHandler(
      get {
        try {

          val stocks: Future[List[Position]] = getStocksFromDb
            .flatMap { stocks =>
              stocks
                .map { stock =>
                  getTransactionsFromDb(stock.symbol)
                    .map { shares => Position(stock, shares) }
                }
                .sequence
            }.unsafeToFuture()

          Directives.onSuccess(stocks)(i => complete(i))
        } catch {
          case e: Exception =>
            println(e)
            failWith(e)
        }

      }
    )

  private def getStockBySymbol(symbol: StockSymbol): Route = {
    corsHandler(
      get {
        val stocks = transactor
          .use { xa => stockRepository.findBySymbol(symbol).transact(xa) }
          .unsafeToFuture()

        Directives.onSuccess(stocks)(i => complete(i))
      }
    )
  }

  private def getMarkets: Route = {
    corsHandler(
      get {
        complete(MarketIndex.values.map(s => Market(s.value, s)))
      }
    )
  }

  private def getSectors: Route = {
    corsHandler(
      get {
        complete(SectorType.values.map(s => Sector(s.name, s)))
      }
    )
  }

  private def getMethods: Route = {
    corsHandler(
      get {
        complete(model.InvestmentMethod.values.map(s => InvestmentMethod(s.name, s)))
      }
    )
  }

  private def getTransactions: Route = {
    corsHandler(
      get {
        val transactions = transactor
          .use { xa => transactionRepository.findAll().transact(xa) }
          .unsafeToFuture()

        Directives.onSuccess(transactions)(i => complete(i))
      }
    )
  }

  case class Market(name: String, `type`: MarketIndex)

  case class Sector(name: String, `type`: SectorType)

  case class InvestmentMethod(name: String, `type`: model.InvestmentMethod)

}