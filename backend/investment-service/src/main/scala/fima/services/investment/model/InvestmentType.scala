package fima.services.investment.model

import fima.services.investment.repository.{EnumSerializer, StringSerializable}

sealed trait InvestmentType extends StringSerializable {
  val name: String

  override def serializeToString: String = name
}

object InvestmentType extends EnumSerializer[InvestmentType] {

  val values = Seq(ValueInvestment, DividentInvestment, GrowthInvestment)

  case object ValueInvestment extends InvestmentType {
    override val name: String = "value"
  }

  case object DividentInvestment extends InvestmentType {
    override val name: String = "divident"
  }

  case object GrowthInvestment extends InvestmentType {
    override val name: String = "growth"
  }

}