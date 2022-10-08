package fima.services.transaction.store

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate

class DateSerializerSpec : StringSpec() {

    init {
        val encoder = mockk<Encoder>(relaxUnitFun = true)
        val decoder = mockk<Decoder>(relaxUnitFun = true)

        val encoded = 20180719
        val decoded = LocalDate.of(2018, 7, 19)

        "it should encode a LocalDate" {
            DateSerializer.serialize(encoder, decoded)
            verify { encoder.encodeInt(encoded) }
        }

        "it should decode a LocalDate" {
            every { decoder.decodeInt() } returns encoded
            DateSerializer.deserialize(decoder) shouldBe decoded
        }
    }

}