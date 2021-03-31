package com.encodeering.luminous.application.api.trade

import java.math.BigDecimal
import java.time.OffsetDateTime

/**
 * @author clausen - encodeering@gmail.com
 */
interface Candle {

    val open: BigDecimal
    val opened: OffsetDateTime

    val close: BigDecimal
    val closed: OffsetDateTime

    val low: BigDecimal
    val high: BigDecimal

    fun realign (timing: (OffsetDateTime, OffsetDateTime) -> Pair<OffsetDateTime, OffsetDateTime>): Candle

}
