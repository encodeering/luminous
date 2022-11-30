package com.encodeering.luminous.system.partner.testing.co

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element

internal fun   TestNumberDefault.asScope () = CoroutineScope (this)

internal class TestNumberDefault (override val number: Int): TestNumber {

    override val key: CoroutineContext.Key<*> get () = TestNumber.Key

}

internal interface TestNumber: Element {

    val number: Int

    companion object Key: CoroutineContext.Key<TestNumber>

}
