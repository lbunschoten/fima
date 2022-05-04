package fima.services.investment.domain

import fima.domain.investment.InvestmentDomain.{Stock => PStock}

import java.time.Instant

case class Stock(
  symbol: StockSymbol,
  name: String,
  index: MarketIndex,
  sector: SectorType,
  investmentType: InvestmentMethod,
  marketValue: BigDecimal,
  updatedAt: Instant
) {

  def stockToProto: PStock = {
    import ProtoConversions.toProto
    PStock("", name, index, toProto(sector), investmentType)
  }

}

case class Position(
  stock: Stock,
  shares: Int
)