package fima.services.investment

import cats.effect.{IO, Resource}
import doobie.Transactor
import doobie.implicits._
import fima.services.investment.InvestmentService.InvestmentServiceGrpc.InvestmentService
import fima.services.investment.InvestmentService.{GetStockRequest, GetStockResponse, GetStocksRequest, GetStocksResponse}
import fima.services.investment.repository.StockRepository

import scala.concurrent.{ExecutionContext, Future}

class InvestmentServiceImpl(stockRepository: StockRepository,
                            transactor: Resource[IO, Transactor[IO]])
                           (private implicit val ec: ExecutionContext) extends InvestmentService {

  override def getStock(request: GetStockRequest): Future[GetStockResponse] = {
    transactor
      .use { xa => stockRepository.findBySymbol(request.symbol).transact(xa) }
      .map { stock => GetStockResponse(stock.map(_.stockToProto)) }
      .unsafeToFuture()
  }

  override def getStocks(request: GetStocksRequest): Future[GetStocksResponse] = {
    transactor
      .use { xa => stockRepository.findAll().transact(xa) }
      .map { stocks => GetStocksResponse(stocks.map(_.stockToProto)) }
      .unsafeToFuture()
  }

}
