package fima.services.investment.domain

import cats.syntax.eq.catsSyntaxEq
import doobie.Meta
import doobie.postgres.implicits.pgEnumStringOpt
import fima.services.investment.repository.{EnumSerialization, StringSerializable}

enum SectorType(val name: String, val value: String) extends StringSerializable {
  case Energy extends SectorType("Energy", "energy")
  case Materials extends SectorType("Materials", "materials")
  case Utilities extends SectorType("Utilities", "utilities")
  case Industrials extends SectorType("Industrials", "industrials")
  case Healthcare extends SectorType("Healthcare", "healthcare")
  case Financials extends SectorType("Financials", "financials")
  case ConsumerDiscretionary extends SectorType("Consumer Discretionary", "consumer_discreditionary")
  case ConsumerStaples extends SectorType("Consumer Staples", "consumer_staples")
  case InformationTechnology extends SectorType("Information Technology", "it")
  case CommunicationServices extends SectorType("Communication Services", "communication")
  case RealEstate extends SectorType("Real Estate", "real_estate")
}

object SectorType extends EnumSerialization[SectorType] {
  val entries: Array[SectorType] = SectorType.values
}