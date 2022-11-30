package com.encodeering.luminous.system.partner.api.marketplace

import java.time.Instant

/**
 * @author clausen - encodeering@gmail.com
 */
data class Asset (
    val isin: String,
    val description: String,
    val price: Double,
    val activity: Activity,
    val stamp: Instant
)

enum class Activity (val probability: Double) {
    LOW    (0.04),
    MEDIUM (0.12),
    HIGH   (0.36)
}
