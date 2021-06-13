package fima.services.investment.market

import cats.effect.{IO, Resource}
import cats.implicits._
import doobie.Transactor
import doobie.implicits._
import fima.services.investment.ApiError
import fima.services.investment.market.alpha_vantage.DailyAdjustedTimeSeriesApiEndpoint.decoders
import fima.services.investment.model.Stock.StockSymbol
import fima.services.investment.repository.StockRepository

class MarketValueUpdater(
  private val stockApi: StockApi,
  private val stockRepository: StockRepository,
  private val transactor: Resource[IO, Transactor[IO]]
) {

  def updateMarketValues(): Unit = {
    println("Updating all market values")
    transactor
      .use { xa => stockRepository.findAll().transact(xa) }
      .flatMap { stocks =>
        stocks
          .map(_.symbol)
          .map(updateMarketValue)
          .flatMap {
            case Left(apiError) =>
              println(apiError.message)
              None
            case Right(v) => Option(v)
          }.sequence
      }
      .unsafeRunAsyncAndForget()
  }

  def updateMarketValue(symbol: StockSymbol): Either[ApiError, IO[Int]] = {
    println(s"Updating market value for symbol: $symbol")
    stockApi.getTimeseries(symbol).map { timeseries: decoders.DailyAdjustedTimeSeries =>
      val (_, latestTimeseries) = timeseries.timeseries.maxBy(_._1)
      transactor.use { xa => stockRepository.updateMarketValue(symbol, latestTimeseries.adjustedClose).transact(xa) }
    }
  }

}
