package com.encodeering.luminous.system.partner.api.io

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @author clausen - encodeering@gmail.com
 */
fun interface Messenger<T> {

    fun send (content: T)

}

fun interface MessengerCo<T> {

    suspend fun send (content: T)

}

fun <T> MessengerCo<T>.asMessenger (scope: CoroutineScope) = Messenger<T> {
    runBlocking {
        scope.launch { send (it) }.join ()
    }
}
