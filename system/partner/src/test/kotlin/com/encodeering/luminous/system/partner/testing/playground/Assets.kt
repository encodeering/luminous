package com.encodeering.luminous.system.partner.testing.playground

import com.encodeering.luminous.system.partner.api.marketplace.Activity
import com.encodeering.luminous.system.partner.api.marketplace.Asset
import java.time.Instant

/**
 * @author clausen - encodeering@gmail.com
 */
internal object Assets {

    fun asset (
        isin: String = "FU1212109727",
        description: String = "qasf fwegdf",
        price: Double = 12.0,
        activity: Activity = Activity.LOW,
        stamp: String = "2022-11-23T14:46:16.042370100Z"
    ) = Asset (isin, description, price, activity, Instant.parse (stamp))

}
