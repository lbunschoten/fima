package fima.services.investment.market.alpha_vantage

import fima.services.investment.ApiError
import fima.services.investment.market.StockApi
import fima.services.investment.market.alpha_vantage.DailyAdjustedTimeSeriesApiEndpoint.decoders.DailyAdjustedTimeSeries
import fima.services.investment.domain.Stock.StockSymbol
import io.circe.parser._
import sttp.client3._

class AlphaVantageApi(baseUrl: String,
                      apiKey: String) extends StockApi {

  def getTimeseries(symbol: StockSymbol): Either[ApiError, DailyAdjustedTimeSeries] = {
    import fima.services.investment.market.alpha_vantage.DailyAdjustedTimeSeriesApiEndpoint._

    val request = basicRequest.get(uri"$baseUrl?function=$functionName&symbol=$symbol&interval=60min&apikey=$apiKey")

    val backend = HttpURLConnectionBackend()

    request.send(backend).body match {
      case Left(error) => Left(ApiError(error))
      case Right(body) =>
        decode[DailyAdjustedTimeSeries](body).left.map { e =>
          ApiError(e.getMessage)
        }
    }
  }
}
