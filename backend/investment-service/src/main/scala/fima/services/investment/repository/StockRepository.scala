package fima.services.investment.repository

import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import doobie.util.log.LogHandler
import fima.services.investment.model.Stock
import fima.services.investment.model.Stock.StockSymbol
import doobie.postgres.implicits.JavaTimeInstantMeta
import doobie.postgres.implicits.JavaTimeLocalDateMeta

class StockRepository {

  private implicit val logHandler: LogHandler = LogHandler(println)

  def insert(stock: Stock): ConnectionIO[Int] = {
    sql"INSERT INTO investment.stock (symbol, name, index, sector, investment_type, market_value, updated_at) VALUES (${stock.symbol}, ${stock.name}, ${stock.index}, ${stock.sector}, ${stock.investmentType}, 0, CURRENT_TIMESTAMP)"
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
