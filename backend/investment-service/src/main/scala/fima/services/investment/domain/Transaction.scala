package fima.services.investment.domain

import fima.services.investment.domain.StockSymbol

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