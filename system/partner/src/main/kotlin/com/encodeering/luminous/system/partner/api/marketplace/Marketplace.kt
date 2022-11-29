package com.encodeering.luminous.system.partner.api.marketplace

/**
 * @author clausen - encodeering@gmail.com
 */
interface Marketplace {

    fun stimulate ()

    fun dump (notifier: Notifier)

    fun subscribe (notifier: Notifier): Runnable

}
