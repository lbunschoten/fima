package fima.services.investment

import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import doobie.util.log.LogHandler
import fima.services.investment.model.Stock
import fima.services.investment.model.Stock.StockSymbol

class StockRepository {

  private implicit val logHandler: LogHandler = LogHandler(println)

  def insert(stock: Stock): ConnectionIO[Int] = {
    sql"insert into investment.stock (symbol, name, index, sector, investment_type) values (${stock.symbol}, ${stock.name}, ${stock.index}, ${stock.sector}, ${stock.investmentType})"
      .update
      .run
  }

  def findBySymbol(symbol: StockSymbol): ConnectionIO[Option[Stock]] = {
    sql"SELECT symbol, name, index, sector, investment_type from investment.stock WHERE symbol = $symbol"
      .query[Stock]
      .option
  }

  def findAll(): ConnectionIO[List[Stock]] = {
    sql"SELECT symbol, name, index, sector, investment_type from investment.stock"
      .query[Stock]
      .to[List]
  }


}
