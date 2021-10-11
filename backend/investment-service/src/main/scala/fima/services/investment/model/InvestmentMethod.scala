package fima.services.investment.model

import enumeratum.values.StringEnumEntry
import fima.services.investment.repository.EnumSerializer

sealed abstract class InvestmentMethod extends StringEnumEntry {
  val name: String
  val value: String
}

object InvestmentMethod extends EnumSerializer[InvestmentMethod] {

  val values: IndexedSeq[InvestmentMethod] = findValues

  case object ValueInvestment extends InvestmentMethod {
    override val name: String = "Value"
    override val value: String = "value"
  }

  case object DividentInvestment extends InvestmentMethod {
    override val name: String = "Divident"
    override val value: String = "divident"
  }

  case object GrowthInvestment extends InvestmentMethod {
    override val name: String = "Growth"
    override val value: String = "growth"
  }

}