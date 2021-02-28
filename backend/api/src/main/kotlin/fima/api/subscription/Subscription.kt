package fima.api.subscription

import fima.api.utils.FromProtoConvertable
import fima.domain.subscription.Subscription as ProtoSubscription

data class Subscription(val name: String) {
    companion object: FromProtoConvertable<ProtoSubscription, Subscription> {
        override fun fromProto(proto: ProtoSubscription): Subscription {
            return Subscription(proto.name)
        }
    }
}

