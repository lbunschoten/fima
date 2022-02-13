package fima.services.investment.market

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.implicits._
import doobie.Transactor
import doobie.implicits._
import fima.services.investment.ApiError
import fima.services.investment.market.alpha_vantage.DailyAdjustedTimeSeriesApiEndpoint.decoders
import fima.services.investment.domain.Stock.StockSymbol
import fima.services.investment.repository.StockRepository

class MarketValueUpdater(
  private val stockApi: StockApi,
  private val stockRepository: StockRepository,
  private val transactor: Transactor[IO]
)(private implicit val ioRuntime: IORuntime) {

  def updateMarketValues(): Unit = {
    println("Updating all market values")
    stockRepository
      .findAll()
      .transact(transactor)
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
      }.unsafeRunAndForget()
  }

  def updateMarketValue(symbol: StockSymbol): Either[ApiError, IO[Int]] = {
    println(s"Updating market value for symbol: $symbol")
    stockApi.getTimeseries(symbol).map { (timeseries: decoders.DailyAdjustedTimeSeries) =>
      val (_, latestTimeseries) = timeseries.timeseries.maxBy(_._1)
      stockRepository.updateMarketValue(symbol, latestTimeseries.adjustedClose).transact(transactor)
    }
  }

}
