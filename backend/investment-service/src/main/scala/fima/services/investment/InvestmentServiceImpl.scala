package fima.services.investment

import cats.effect.{IO, Resource}
import doobie.Transactor
import doobie.implicits._
import fima.services.investment.InvestmentService.InvestmentServiceGrpc.InvestmentService
import fima.services.investment.InvestmentService.{GetStockRequest, GetStockResponse, GetStocksRequest, GetStocksResponse}

import scala.concurrent.{ExecutionContext, Future}

class InvestmentServiceImpl(stockRepository: StockRepository,
                            transactor: Resource[IO, Transactor[IO]])
                           (private implicit val ec: ExecutionContext) extends InvestmentService {

  override def getStock(request: GetStockRequest): Future[GetStockResponse] = {
    val response = for {
      stocks <- transactor.use { xa => stockRepository.findBySymbol(request.symbol).transact(xa) }
    } yield GetStockResponse(stocks.map(_.stockToProto))

    response.unsafeToFuture()
  }

  override def getStocks(request: GetStocksRequest): Future[GetStocksResponse] = {
    val response = for {
      stocks <- transactor.use { xa => stockRepository.findAll().transact(xa) }
    } yield GetStocksResponse(stocks.map(_.stockToProto))

    response.unsafeToFuture()
  }

}
