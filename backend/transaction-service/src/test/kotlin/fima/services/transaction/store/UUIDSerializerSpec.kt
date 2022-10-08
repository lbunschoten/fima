package fima.services.transaction.store

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

class UUIDSerializerSpec : StringSpec() {

    init {
        val encoder = mockk<Encoder>(relaxUnitFun = true)
        val decoder = mockk<Decoder>(relaxUnitFun = true)

        val decoded = UUID.randomUUID()
        val encoded = decoded.toString()

        "it should encode a UUID" {
            UUIDSerializer.serialize(encoder, decoded)
            verify { encoder.encodeString(encoded) }
        }

        "it should decode a UUID" {
            every { decoder.decodeString() } returns encoded
            UUIDSerializer.deserialize(decoder) shouldBe decoded
        }
    }

}