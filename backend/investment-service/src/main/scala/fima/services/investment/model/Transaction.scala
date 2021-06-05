package fima.services.investment.model

import fima.services.investment.model.Stock.StockSymbol

import java.time.LocalDate

case class Transaction(date: LocalDate,
                       stockSymbol: StockSymbol,
                       shares: Int,
                       pricePerShare: BigDecimal,
                       commissionCost: BigDecimal,
                       currencyChangeCost: BigDecimal
                      )