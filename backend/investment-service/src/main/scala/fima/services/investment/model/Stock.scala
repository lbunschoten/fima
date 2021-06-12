package fima.services.investment.model

import fima.domain.investment.InvestmentDomain.{Stock => PStock}
import fima.services.investment.model.Stock.StockSymbol

import java.time.Instant

object Stock {
  type StockSymbol = String
}

case class Stock(
  symbol: StockSymbol,
  name: String,
  index: MarketIndex,
  sector: Sector,
  investmentType: InvestmentType,
  marketValue: BigDecimal,
  updatedAt: Instant
) {

  def stockToProto: PStock = {
    import implicits._

    PStock(symbol, name, index, toProto(sector), investmentType)
  }

}