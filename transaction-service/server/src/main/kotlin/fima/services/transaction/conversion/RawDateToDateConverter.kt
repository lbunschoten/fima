package fima.services.transaction.conversion

import fima.domain.transaction.Date

class RawDateToDateConverter : (Int) -> Date {

  override fun invoke(rawDate: Int): Date {
    return Date.newBuilder().run {
      day = rawDate.toString().substring(6, 8).toInt()
      month = rawDate.toString().substring(4, 6).toInt()
      year = rawDate.toString().substring(0, 4).toInt()
      build()
    }
  }

}