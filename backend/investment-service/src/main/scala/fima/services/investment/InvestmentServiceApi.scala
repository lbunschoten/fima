package fima.services.investment

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import cats.effect.{IO, Resource}
import doobie.Transactor
import doobie.implicits._
import fima.services.investment.model.Stock.StockSymbol
import fima.services.investment.model.{InvestmentType, MarketIndex, Sector, Stock}
import fima.services.investment.repository.StockRepository
import io.circe.generic.extras.semiauto.deriveEnumerationCodec
import io.circe.{Codec, Encoder}

import scala.concurrent.ExecutionContext

class InvestmentServiceApi(
  private val stockRepository: StockRepository,
  transactor: Resource[IO, Transactor[IO]]
)(private implicit val ec: ExecutionContext) {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.semiauto.deriveEncoder

  implicit val sectorEncoder: Codec[Sector] = deriveEnumerationCodec[Sector]
  implicit val investmentTypeEncoder: Codec[InvestmentType] = deriveEnumerationCodec[InvestmentType]
  implicit val modeCodec: Codec[MarketIndex] = deriveEnumerationCodec[MarketIndex]
  implicit val stockEncoder: Encoder[Stock] = deriveEncoder[Stock]

  val routes: Route =
    concat(
      path("stocks")(getStocks),
      path("stock" / Remaining)(getStockBySymbol)
    )

  private def getStocks: Route =
    get {
      val stocks = transactor
        .use { xa => stockRepository.findAll().transact(xa) }
        .unsafeToFuture()

      Directives.onSuccess(stocks)(i => complete(i))
    }

  private def getStockBySymbol(symbol: StockSymbol): Route =
    get {
      val stocks = transactor
        .use { xa => stockRepository.findBySymbol(symbol).transact(xa) }
        .unsafeToFuture()

      Directives.onSuccess(stocks)(i => complete(i))
    }

}