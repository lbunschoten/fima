package fima.services.investment

import cats.effect.IO
import doobie.Transactor
import doobie.implicits._
import fima.services.investment.InvestmentService._
import fima.services.investment.repository.StockRepository
import fima.services.investment.domain.StockSymbol
import io.grpc.Metadata

class InvestmentServiceImpl(stockRepository: StockRepository,
                            transactor: Transactor[IO]) extends InvestmentServiceFs2Grpc[IO, Metadata] {

  override def getStock(request: GetStockRequest, metadata: Metadata): IO[GetStockResponse] = {
    stockRepository
      .findBySymbol(request.symbol)
      .transact(transactor)
      .map { stock => GetStockResponse(stock.map(_.stockToProto)) }
  }

  override def getStocks(request: GetStocksRequest, metadata: Metadata): IO[GetStocksResponse] = {
    stockRepository
      .findAll()
      .transact(transactor)
      .map { stocks => GetStocksResponse(stocks.map(_.stockToProto)) }
  }

}
