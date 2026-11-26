package com.data_realm.dunning.domain

import java.time.ZoneId

data class Customer(
    val id: String,
    val name: String,
    val email: String,
    val zone: ZoneId,
    val plan: PlanTier,
)

enum class PlanTier(
    /**
     * True for tiers from the pre-2023 price list. Sales and support call these the "legacy plans";
     * in billing code they are "grandfathered". Grandfathered customers keep their original terms.
     */
    val grandfathered: Boolean,
) {
    STARTER(false),
    GROWTH(false),
    SCALE(false),
    CLASSIC_STARTER(true),
    CLASSIC_GROWTH(true),
}
