package fima.services.investment.repository

import cats.implicits.catsSyntaxEq
import doobie.Meta
import doobie.postgres.implicits.pgEnumStringOpt

trait EnumSerialization[T <: StringSerializable] {
  val entries: Array[T]

  def toEnum(t: T): String = t.value

  def fromEnum(serializedEnum: String): Option[T] = entries.find(_.value === serializedEnum)

  val meta: Meta[T] = pgEnumStringOpt(getClass.getSimpleName, fromEnum, toEnum)
}