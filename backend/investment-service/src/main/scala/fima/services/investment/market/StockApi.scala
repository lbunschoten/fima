package fima.services.investment.market

import fima.services.investment.ApiError
import fima.services.investment.market.alpha_vantage.WeeklyAdjustedTimeSeriesApiEndpoint.decoders.WeeklyAdjustedTimeSeries
import fima.services.investment.domain.StockSymbol

trait StockApi {
  def getTimeseries(symbol: StockSymbol): Either[ApiError, WeeklyAdjustedTimeSeries]
}
