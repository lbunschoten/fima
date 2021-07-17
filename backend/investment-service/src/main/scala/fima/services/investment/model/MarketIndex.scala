package fima.services.investment.model

import fima.domain.investment.InvestmentDomain
import fima.services.investment.repository.{EnumSerializer, StringSerializable}

import scala.language.implicitConversions

sealed trait MarketIndex extends StringSerializable {
  val name: String

  override def serializeToString: String = name
}

object MarketIndex extends EnumSerializer[MarketIndex] {

  val values = Seq(NYSE, NASDAQ, AMS)

  case object NYSE extends MarketIndex {
    val name = "NYSE"
  }

  case object NASDAQ extends MarketIndex {
    val name = "NASDAQ"
  }

  case object AMS extends MarketIndex {
    val name = "AMS"
  }

  object implicits {
    implicit def toProto(marketIndex: MarketIndex): InvestmentDomain.MarketIndex = {
      InvestmentDomain.MarketIndex.fromName(marketIndex.serializeToString.toUpperCase).get
    }
  }
}