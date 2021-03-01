package fima.api.subscription

import fima.api.utils.FromProtoConvertable
import java.util.UUID
import fima.domain.subscription.Subscription as ProtoSubscription

data class Subscription(val id: UUID, val name: String) {
    companion object: FromProtoConvertable<ProtoSubscription, Subscription> {
        override fun fromProto(proto: ProtoSubscription): Subscription {
            return Subscription(UUID.fromString(proto.id), proto.name)
        }
    }
}

