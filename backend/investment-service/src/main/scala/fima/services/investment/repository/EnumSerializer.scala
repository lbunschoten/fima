package fima.services.investment.repository

import doobie.Meta
import doobie.postgres.implicits.pgEnumStringOpt

trait EnumSerializer[T <: StringSerializable] {
  val values: Seq[T]

  def toEnum(t: T): String = t.serializeToString

  def fromEnum(serializedEnum: String): Option[T] = values.find(_.serializeToString == serializedEnum)

  implicit val meta: Meta[T] = pgEnumStringOpt(getClass.getSimpleName, fromEnum, toEnum)
}

trait StringSerializable {
  def serializeToString: String
}
