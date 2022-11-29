package com.encodeering.luminous.system.partner.internal.ws

import com.encodeering.luminous.system.partner.api.io.Messenger

/**
 * @author clausen - encodeering@gmail.com
 */
internal interface WebsocketStream<T> {

    val stream: Messenger<T>

    suspend fun use (f: suspend WebsocketStream<T>.() -> Unit)

}

internal interface WebsocketStreamFactory<K, T> {

    fun create (key: K, messenger: Messenger<String>): WebsocketStream<T>

}
