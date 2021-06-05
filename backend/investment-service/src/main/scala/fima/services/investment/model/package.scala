package fima.services.investment

import fima.domain.investment.InvestmentDomain

import scala.language.implicitConversions

package object model {

  object implicits {

    implicit def toProto(sector: Sector): InvestmentDomain.Sector = {
      InvestmentDomain.Sector.fromName(sector.serializeToString.toUpperCase).get
    }

    implicit def toProto(marketIndex: MarketIndex): InvestmentDomain.MarketIndex = {
      InvestmentDomain.MarketIndex.fromName(marketIndex.serializeToString.toUpperCase).get
    }

    implicit def toProto(investmentType: InvestmentType): InvestmentDomain.InvestmentType = {
      InvestmentDomain.InvestmentType.fromName(investmentType.serializeToString.toUpperCase).get
    }
  }

}
