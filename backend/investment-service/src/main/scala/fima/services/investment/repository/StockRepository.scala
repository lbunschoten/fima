package fima.services.investment.repository

import cats.effect.IO
import doobie.{ConnectionIO, Meta}
import doobie.implicits.toSqlInterpolator
import doobie.*
import doobie.implicits.*
import doobie.util.log.{LogEvent, LogHandler}
import fima.services.investment.domain.{InvestmentMethod, MarketIndex, SectorType, Stock}
import fima.services.investment.domain.StockSymbol
import fima.services.investment.domain.MarketIndex.meta
import doobie.postgres.implicits.JavaTimeInstantMeta
import doobie.postgres.implicits.JavaTimeLocalDateMeta
import doobie.util.Get
import doobie.util.meta.Meta
import fima.services.investment.domain.*

class StockRepository {

  private implicit val logHandler: LogHandler[IO] = new LogHandler[IO] {
    def run(logEvent: LogEvent): IO[Unit] =
      IO {
        println(logEvent.sql)
      }
  }

  private implicit val marketIndexMeta: Meta[MarketIndex] = fima.services.investment.domain.MarketIndex.meta
  private implicit val sectorTypeMeta: Meta[SectorType] = fima.services.investment.domain.SectorType.meta
  private implicit val investmentMethodMeta: Meta[InvestmentMethod] = fima.services.investment.domain.InvestmentMethod.meta

  def insert(stock: Stock): ConnectionIO[Int] = {
    sql"INSERT INTO investment.stock (symbol, name, index, sector, investment_type, market_value, updated_at) VALUES (${stock.symbol}, ${stock.name}, ${stock.index.value}, ${stock.sector.value}, ${stock.investmentType.value}, 0, CURRENT_TIMESTAMP)"
      .update
      .run
  }

  def findBySymbol(symbol: StockSymbol): ConnectionIO[Option[Stock]] = {
    sql"SELECT symbol, name, index, sector, investment_type, market_value, updated_at FROM investment.stock WHERE symbol = $symbol"
      .query[Stock]
      .option
  }

  def findAll(): ConnectionIO[List[Stock]] = {
    sql"SELECT symbol, name, index, sector, investment_type, market_value, updated_at FROM investment.stock"
      .query[Stock]
      .to[List]
  }

  def updateMarketValue(symbol: StockSymbol, marketValue: BigDecimal): ConnectionIO[Int] = {
    sql"UPDATE investment.stock SET market_value = $marketValue, updated_at = CURRENT_TIMESTAMP WHERE symbol = $symbol"
      .update
      .run
  }

}
