package fima.services.investment

import io.circe.{Decoder, KeyDecoder}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try

package object market {
  private val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  case class MetaData(information: String, symbol: String, lastRefreshed: LocalDateTime, interval: String, outputSize: String, timezone: String)

  case class TimeSeries(open: BigDecimal, high: BigDecimal, low: BigDecimal, close: BigDecimal, volume: Int)

  case class StockTimeSeries(metadata: MetaData, timeseries: Map[LocalDateTime, TimeSeries])

  implicit val decodeDateTime: Decoder[LocalDateTime] = Decoder.decodeString.emapTry { str =>
    Try(LocalDateTime.parse(str, dateTimeFormat))
  }

  implicit val timeSeriesDecoder: Decoder[TimeSeries] = Decoder.forProduct5(
    "1. open",
    "2. high",
    "3. low",
    "4. close",
    "5. volume"
  )(TimeSeries.apply)

  implicit val metaDataDecoder: Decoder[MetaData] = Decoder.forProduct6(
    "1. Information",
    "2. Symbol",
    "3. Last Refreshed",
    "4. Interval",
    "5. Output Size",
    "6. Time Zone"
  )(MetaData.apply)

  implicit val timeseriesKeyDecoder: KeyDecoder[LocalDateTime] = (key: String) => Option(LocalDateTime.parse(key, dateTimeFormat))

  implicit val timeseriesCollectionDecoder: Decoder[Map[LocalDateTime, TimeSeries]] = Decoder.decodeMap[LocalDateTime, TimeSeries]

  implicit val stockInfoDecoder: Decoder[StockTimeSeries] = Decoder.forProduct2(
    "Meta Data",
    "Time Series (5min)"
  )(StockTimeSeries.apply)

}

case class ApiError(message: String)