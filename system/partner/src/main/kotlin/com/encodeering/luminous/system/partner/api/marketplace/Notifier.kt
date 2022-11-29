package com.encodeering.luminous.system.partner.api.marketplace

/**
 * @author clausen - encodeering@gmail.com
 */
fun interface Notifier {

    enum class Code {
        CREATE,
        DELETE,
        UPDATE
    }

    fun notify (code: Code, asset: Asset)

}
