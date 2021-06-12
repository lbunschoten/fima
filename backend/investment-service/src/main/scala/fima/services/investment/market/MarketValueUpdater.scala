package fima.services.investment.market

import cats.effect.{IO, Resource}
import doobie.Transactor
import doobie.implicits._
import fima.services.investment.model.Stock.StockSymbol
import fima.services.investment.repository.StockRepository

class MarketValueUpdater(private val stockApi: StockApi,
                         private val stockRepository: StockRepository,
                         private val transactor: Resource[IO, Transactor[IO]]) {

  def updateMarketValues(): Unit = {
    println("Updating all market values")
    transactor
      .use { xa => stockRepository.findAll().transact(xa) }
      .map { stocks =>
        stocks
          .map(_.symbol)
          .toSet
          .foreach(updateMarketValue)
      }
      .unsafeRunAsyncAndForget()
  }

  def updateMarketValue(symbol: StockSymbol): Unit = {
    println(s"Updating market value for symbol: $symbol")
    stockApi.getTimeseries(symbol).map { timeseries =>
      val (_, timeSeries) = timeseries.timeseries.head
      stockRepository.updateMarketValue(symbol, timeSeries.adjustedClose)
    }
  }

}
