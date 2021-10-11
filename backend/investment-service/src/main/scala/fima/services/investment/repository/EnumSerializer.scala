package fima.services.investment.repository

import doobie.Meta
import doobie.postgres.implicits.pgEnumStringOpt
import enumeratum.values.{StringCirceEnum, StringEnum, StringEnumEntry}

trait EnumSerializer[T <: StringEnumEntry] extends StringEnum[T] with StringCirceEnum[T] {
  def toEnum(t: T): String = t.value

  def fromEnum(serializedEnum: String): Option[T] = values.find(_.value == serializedEnum)

  implicit val meta: Meta[T] = pgEnumStringOpt(getClass.getSimpleName, fromEnum, toEnum)
}
