package fima.services.investment

import doobie.Meta
import doobie.postgres.implicits.pgEnumStringOpt
import fima.domain.investment.InvestmentDomain

package object domain {

  object implicits {

    implicit def toProto(sectorKey: SectorType): InvestmentDomain.Sector = {
      InvestmentDomain.Sector.fromName(sectorKey.value.toUpperCase).get
    }

    implicit def toProto(marketIndex: MarketIndex): InvestmentDomain.MarketIndex = {
      InvestmentDomain.MarketIndex.fromName(marketIndex.value.toUpperCase).get
    }

    implicit def toProto(investmentType: InvestmentMethod): InvestmentDomain.InvestmentType = {
      InvestmentDomain.InvestmentType.fromName(investmentType.value.toUpperCase).get
    }

    implicit val sectorTypeMeta: Meta[SectorType] = SectorType.meta
    implicit val marketIndexMeta: Meta[MarketIndex] = MarketIndex.meta
    implicit val investmentTypeMeta: Meta[InvestmentMethod] = InvestmentMethod.meta
  }

}
