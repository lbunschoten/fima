package fima.api.subscription

import fima.api.utils.FromProtoConvertable
import fima.domain.subscription.Recurrence
import fima.domain.transaction.Transaction
import java.util.TreeSet
import java.util.UUID
import fima.domain.subscription.Subscription as ProtoSubscription

data class Subscription(val id: UUID, val name: String, val recurrence: Recurrence) {
    companion object: FromProtoConvertable<ProtoSubscription, Subscription> {
        override fun fromProto(proto: ProtoSubscription): Subscription {
            return Subscription(UUID.fromString(proto.id), proto.name, proto.recurrence)
        }
    }
}
