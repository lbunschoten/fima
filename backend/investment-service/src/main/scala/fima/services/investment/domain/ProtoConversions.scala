package fima.services.investment.domain

import doobie.Meta
import doobie.postgres.implicits
import doobie.postgres.implicits.pgEnumStringOpt
import doobie.util.Get
import fima.domain.investment.InvestmentDomain

object ProtoConversions {

  implicit def toProto(sectorKey: SectorType): InvestmentDomain.Sector = {
    InvestmentDomain.Sector.fromName(sectorKey.value.toUpperCase).get
  }

  implicit def toProto(marketIndex: MarketIndex): InvestmentDomain.MarketIndex = {
    InvestmentDomain.MarketIndex.fromName(marketIndex.value.toUpperCase).get
  }

  implicit def toProto(investmentType: InvestmentMethod): InvestmentDomain.InvestmentType = {
    InvestmentDomain.InvestmentType.fromName(investmentType.value.toUpperCase).get
  }
}
