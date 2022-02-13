package fima.services.investment.domain

import fima.services.investment.repository.{EnumSerialization, StringSerializable}

enum InvestmentMethod(val name: String, override val value: String) extends StringSerializable {
  case ValueInvestment extends InvestmentMethod("Value", "value")
  case DividentInvestment extends InvestmentMethod("Divident", "divident")
  case GrowthInvestment extends InvestmentMethod("Growth", "growth")
}

object InvestmentMethod extends EnumSerialization[InvestmentMethod] {
  val entries: Array[InvestmentMethod] = InvestmentMethod.values
}
