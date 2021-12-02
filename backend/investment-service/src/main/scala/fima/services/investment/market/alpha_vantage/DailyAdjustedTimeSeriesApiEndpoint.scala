package fima.services.investment.market.alpha_vantage

import io.circe.{Decoder, KeyDecoder}

import java.time.{LocalDate, LocalDateTime}
import scala.util.Try

object DailyAdjustedTimeSeriesApiEndpoint extends ApiEndpoint {

  override val functionName: String = "TIME_SERIES_DAILY_ADJUSTED"

  object decoders {

    case class DailyAdjustedTimeSeries(metadata: MetaData, timeseries: Map[LocalDate, TimeSeries])

    case class MetaData(
      information: String,
      symbol: String,
      lastRefreshed: LocalDateTime,
      outputSize: String,
      timezone: String
    )

    case class TimeSeries(
      open: BigDecimal,
      high: BigDecimal,
      low: BigDecimal,
      close: BigDecimal,
      adjustedClose: BigDecimal,
      volume: Int,
      dividentAmount: BigDecimal,
      splitCoefficient: BigDecimal
    )

    implicit val localDateDecoder: Decoder[LocalDate] = Decoder.decodeString.emapTry { str =>
      Try(LocalDate.parse(str))
    }

    implicit val localDateTimeDecoder: Decoder[LocalDateTime] = Decoder.decodeString.emapTry { str =>
      Try(LocalDateTime.parse(str))
    }

    implicit val timeSeriesDecoder: Decoder[TimeSeries] = Decoder.forProduct8(
      "1. open",
      "2. high",
      "3. low",
      "4. close",
      "5. adjusted close",
      "6. volume",
      "7. dividend amount",
      "8. split coefficient"
    )(TimeSeries.apply)

    implicit val metaDataDecoder: Decoder[MetaData] = Decoder.forProduct5(
      "1. Information",
      "2. Symbol",
      "3. Last Refreshed",
      "4. Output Size",
      "5. Time Zone"
    )(MetaData.apply)

    implicit val timeSeriesKeyDecoder: KeyDecoder[LocalDate] = (key: String) => Option(LocalDate.parse(key))

    implicit val timeSeriesMapDecoder: Decoder[Map[LocalDate, TimeSeries]] = Decoder.decodeMap[LocalDate, TimeSeries]

    implicit val dailyAdjustedTimeSeriesDecoder: Decoder[DailyAdjustedTimeSeries] = Decoder.forProduct2(
      "Meta Data",
      "Time Series (Daily)"
    )(DailyAdjustedTimeSeries.apply)
  }
}
