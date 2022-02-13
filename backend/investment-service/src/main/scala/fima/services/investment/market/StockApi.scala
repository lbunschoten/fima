package fima.services.investment.market

import fima.services.investment.ApiError
import fima.services.investment.market.alpha_vantage.DailyAdjustedTimeSeriesApiEndpoint.decoders.DailyAdjustedTimeSeries
import fima.services.investment.domain.Stock.StockSymbol

trait StockApi {
  def getTimeseries(symbol: StockSymbol): Either[ApiError, DailyAdjustedTimeSeries]
}
