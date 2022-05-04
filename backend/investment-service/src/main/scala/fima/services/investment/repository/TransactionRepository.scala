package fima.services.investment.repository

import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import doobie.postgres.implicits.JavaTimeInstantMeta
import doobie.postgres.implicits.JavaTimeLocalDateMeta
import doobie.postgres.implicits.UuidType
import doobie.util.log.LogHandler
import fima.services.investment.domain.StockSymbol
import fima.services.investment.domain.Transaction

class TransactionRepository {

  private implicit val logHandler: LogHandler = LogHandler(println)

  def insert(transaction: Transaction): ConnectionIO[Int] = {
    sql"INSERT INTO investment.transaction(symbol, date, shares, price_per_share, commission_cost, currency_exchange_cost, updated_at) VALUES (${transaction.stockSymbol}, ${transaction.date}, ${transaction.shares}, ${transaction.pricePerShare}, ${transaction.commissionCost}, ${transaction.currencyExchangeCost}, CURRENT_TIMESTAMP)"
      .update
      .run
  }

  def findBySymbol(symbol: StockSymbol): ConnectionIO[List[Transaction]] = {
    sql"SELECT id, symbol, date, shares, price_per_share, commission_cost, currency_exchange_cost, updated_at FROM investment.transaction WHERE symbol = $symbol"
      .query[Transaction]
      .to[List]
  }

  def findAll(): ConnectionIO[List[Transaction]] = {
    sql"SELECT id, symbol, date, shares, price_per_share, commission_cost, currency_exchange_cost, updated_at FROM investment.transaction"
      .query[Transaction]
      .to[List]
  }

}
