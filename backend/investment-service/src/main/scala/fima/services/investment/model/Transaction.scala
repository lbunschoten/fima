package fima.services.investment.model

import fima.services.investment.model.Stock.StockSymbol

import java.time.LocalDate
import java.util.UUID

case class Transaction(
  id: UUID,
  stockSymbol: StockSymbol,
  date: LocalDate,
  shares: Int,
  pricePerShare: BigDecimal,
  commissionCost: BigDecimal,
  currencyExchangeCost: BigDecimal
)