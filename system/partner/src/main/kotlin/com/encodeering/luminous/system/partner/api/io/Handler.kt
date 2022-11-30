package com.encodeering.luminous.system.partner.api.io

/**
 * @author clausen - encodeering@gmail.com
 */
interface Handler<T> {

    fun T.handle ()

}

interface HandlerCo<T> {

    suspend fun T.handle ()

}
