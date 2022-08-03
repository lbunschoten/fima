package fima.services.subscription.domain

import java.util.UUID

case class Subscription(id: UUID, name: String, query: SubscriptionSearchQuery, recurrence: Recurrence)