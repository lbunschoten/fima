package fima.services.investment.model

import fima.domain.investment.InvestmentDomain
import fima.services.investment.repository.{EnumSerializer, StringSerializable}

import scala.language.implicitConversions

sealed trait Sector extends StringSerializable

object Sector extends EnumSerializer[Sector] {
  override val values: Seq[Sector] = Seq(
    Energy, Materials, Utilities, Industrials, Healthcare, Financials, ConsumerDiscretionary,
    ConsumerStaples, InformationTechnology, CommunicationServices, RealEstate
  )
}

case object Energy extends Sector {
  override def serializeToString: String = "energy"
}

case object Materials extends Sector {
  override def serializeToString: String = "materials"
}

case object Utilities extends Sector {
  override def serializeToString: String = "utilities"
}

case object Industrials extends Sector {
  override def serializeToString: String = "industrials"
}

case object Healthcare extends Sector {
  override def serializeToString: String = "healthcare"
}

case object Financials extends Sector {
  override def serializeToString: String = "financials"
}

case object ConsumerDiscretionary extends Sector {
  override def serializeToString: String = "consumer_discreditionary"
}

case object ConsumerStaples extends Sector {
  override def serializeToString: String = "consumer_staples"
}

case object InformationTechnology extends Sector {
  override def serializeToString: String = "it"
}

case object CommunicationServices extends Sector {
  override def serializeToString: String = "communication"
}

case object RealEstate extends Sector {
  override def serializeToString: String = "real_estate"
}
