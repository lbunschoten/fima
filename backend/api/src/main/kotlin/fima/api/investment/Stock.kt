package fima.api.investment

import fima.api.utils.FromProtoConvertable
import fima.domain.investment.Stock as ProtoStock

data class Stock(val symbol: String, val name: String, val index: String, val sector: String, val investmentType: String) {
    companion object : FromProtoConvertable<ProtoStock, Stock> {
        override fun fromProto(proto: ProtoStock): Stock {
            return Stock(proto.symbol, proto.name, proto.index.name, proto.sector.name, proto.investmentType.name)
        }
    }
}
