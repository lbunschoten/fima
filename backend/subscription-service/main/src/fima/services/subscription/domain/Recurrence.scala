package fima.services.subscription.domain

import cats.implicits.catsSyntaxEq

sealed trait Recurrence {
  val id: Int
  val name: String
}

object Recurrence {

  val values: Seq[Recurrence] = Seq(Monthly, Yearly)

  case object Monthly extends Recurrence {
    override val id: Int = 0
    override val name: String = "monthly"
  }

  case object Yearly extends Recurrence {
    override val id: Int = 1
    override val name: String = "yearly"
  }

  def toEnum(e: Recurrence): String = e.name

  def fromEnum(name: String): Option[Recurrence] = values.find(_.name === name)

}