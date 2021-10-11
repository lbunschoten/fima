package fima.services.investment.model

import enumeratum.values.StringEnumEntry
import fima.domain.investment.InvestmentDomain
import fima.services.investment.repository.EnumSerializer

import scala.language.implicitConversions

sealed abstract class MarketIndex extends StringEnumEntry {
  val value: String
}

object MarketIndex extends EnumSerializer[MarketIndex] {

  val values: IndexedSeq[MarketIndex] = findValues

  case object NYSE extends MarketIndex {
    val value = "NYSE"
  }

  case object NASDAQ extends MarketIndex {
    val value = "NASDAQ"
  }

  case object AMS extends MarketIndex {
    val value = "AMS"
  }

  object implicits {
    implicit def toProto(marketIndex: MarketIndex): InvestmentDomain.MarketIndex = {
      InvestmentDomain.MarketIndex.fromName(marketIndex.value.toUpperCase).get
    }
  }
}