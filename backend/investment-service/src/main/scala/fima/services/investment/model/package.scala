package fima.services.investment

import fima.domain.investment.InvestmentDomain
import io.circe.generic.extras.Configuration

import scala.language.implicitConversions

package object model {

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
  }

}
