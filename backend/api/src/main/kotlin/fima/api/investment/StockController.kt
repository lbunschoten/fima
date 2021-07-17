package fima.api.investment

import fima.services.investment.GetStockRequest
import fima.services.investment.GetStocksRequest
import fima.services.investment.InvestmentServiceGrpcKt
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class StockController @Autowired constructor(
    private val investmentService: InvestmentServiceGrpcKt.InvestmentServiceCoroutineStub,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @CrossOrigin
    @GetMapping("/stock/{symbol}")
    suspend fun getStock(@PathVariable("symbol") symbol: String): Stock {
        logger.info("Received request to get stock for symbol $symbol")

        val request = GetStockRequest.newBuilder().setSymbol(symbol).build()
        val response = investmentService.getStock(request)

        if (response.hasStock()) {
            return Stock.fromProto(response.stock)
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    @CrossOrigin
    @GetMapping("/stocks")
    suspend fun getStocks(): List<Stock> {
        logger.info("Received request to get investments")

        val request = GetStocksRequest.newBuilder().build()

        return investmentService
            .getStocks(request)
            .stocksList
            .map(Stock::fromProto)
    }
}