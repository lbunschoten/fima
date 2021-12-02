package fima.services.transaction.conversion

import fima.domain.transaction.Date
import fima.domain.transaction.date

class RawDateToDateConverter : (Int) -> Date {

    override fun invoke(rawDate: Int): Date {
        return date {
            day = rawDate.toString().substring(6, 8).toInt()
            month = rawDate.toString().substring(4, 6).toInt()
            year = rawDate.toString().substring(0, 4).toInt()
        }
    }

}