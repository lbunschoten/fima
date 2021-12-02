package fima.services.investment

import cats.effect.IO
import doobie.Transactor
import doobie.implicits._
import fima.services.investment.InvestmentService._
import fima.services.investment.repository.StockRepository
import io.grpc.Metadata

import scala.concurrent.ExecutionContext

class InvestmentServiceImpl(stockRepository: StockRepository,
                            transactor: Transactor[IO])
                           (private implicit val ec: ExecutionContext) extends InvestmentServiceFs2Grpc[IO, Metadata] {

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
