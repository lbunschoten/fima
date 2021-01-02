package fima.services.transaction.conversion

import fima.domain.transaction.Date
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RawDateToDateConverterSpec : StringSpec() {

    init {
        val converter = RawDateToDateConverter()

        "it should convert a rawDate to a date" {
            converter(20180719) shouldBe Date.newBuilder().run {
                day = 19
                month = 7
                year = 2018
                build()
            }
        }

    }

}