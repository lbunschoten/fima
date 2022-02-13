package fima.services.investment.domain

import cats.syntax.eq.catsSyntaxEq
import doobie.Meta
import doobie.postgres.implicits.pgEnumStringOpt
import fima.domain.investment.InvestmentDomain
import fima.services.investment.repository.{EnumSerialization, StringSerializable}

enum MarketIndex(override val value: String) extends StringSerializable {
  case NYSE extends MarketIndex("NYSE")
  case NASDAQ extends MarketIndex("NASDAQ")
  case AMS extends MarketIndex("AMS")
}

object MarketIndex extends EnumSerialization[MarketIndex] {
  val entries: Array[MarketIndex] = MarketIndex.values
}