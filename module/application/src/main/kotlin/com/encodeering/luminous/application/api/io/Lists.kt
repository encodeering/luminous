@file:JvmName ("Lists")
package com.encodeering.luminous.application.api.io

/**
 * @author clausen - encodeering@gmail.com
 */
fun <T> Collection<T>.reduce (n: Int, f: (MutableList<T>, T) -> MutableList<T>): List<T> {
    val drop = drop (n)
    val take = take (n)

    return when {
        drop.isEmpty () -> drop
        else            -> drop.fold (take.toMutableList (), f)
    }
}
