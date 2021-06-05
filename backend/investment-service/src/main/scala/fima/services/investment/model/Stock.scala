package fima.services.investment.model

import fima.services.investment.model.Stock.StockSymbol
import fima.domain.investment.InvestmentDomain.{Stock => PStock}

object Stock {
  type StockSymbol = String
}

case class Stock(symbol: StockSymbol,
                 name: String,
                 index: MarketIndex,
                 sector: Sector,
                 investmentType: InvestmentType) {

  def stockToProto: PStock = {
    import implicits._

    PStock(symbol, name, index, toProto(sector), investmentType)
  }

}