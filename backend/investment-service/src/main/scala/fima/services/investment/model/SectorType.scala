package fima.services.investment.model

import enumeratum.values.StringEnumEntry
import fima.services.investment.repository.EnumSerializer

sealed abstract class SectorType extends StringEnumEntry {
  val name: String
  val value: String
}

object SectorType extends EnumSerializer[SectorType] {

  val values: IndexedSeq[SectorType] = findValues

  case object Energy extends SectorType {
    override val name: String = "Energy"
    override val value: String = "energy"
  }

  case object Materials extends SectorType {
    override val name: String = "Materials"
    override val value: String = "materials"
  }

  case object Utilities extends SectorType {
    override val name: String = "Utilities"
    override val value: String = "utilities"
  }

  case object Industrials extends SectorType {
    override val name: String = "Industrials"
    override val value: String = "industrials"
  }

  case object Healthcare extends SectorType {
    override val name: String = "Healthcare"
    override val value: String = "healthcare"
  }

  case object Financials extends SectorType {
    override val name: String = "Financials"
    override val value: String = "financials"
  }

  case object ConsumerDiscretionary extends SectorType {
    override val name: String = "Consumer Discretionary"
    override val value: String = "consumer_discreditionary"
  }

  case object ConsumerStaples extends SectorType {
    override val name: String = "Consumer Staples"
    override val value: String = "consumer_staples"
  }

  case object InformationTechnology extends SectorType {
    override val name: String = "Information Technology"
    override val value: String = "it"
  }

  case object CommunicationServices extends SectorType {
    override val name: String = "Communication Services"
    override val value: String = "communication"
  }

  case object RealEstate extends SectorType {
    override val name: String = "Real Estate"
    override val value: String = "real_estate"
  }

}
