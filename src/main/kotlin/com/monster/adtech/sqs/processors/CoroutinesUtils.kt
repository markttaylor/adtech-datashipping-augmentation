/*
This code is from here: https://jivimberg.io/blog/2019/02/23/sqs-consumer-using-kotlin-coroutines/
No copyright
*/

package com.monster.adtech.sqs.processors

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import java.lang.Thread.currentThread

suspend fun CoroutineScope.repeatUntilCancelled(block: suspend () -> Unit) {
    while (isActive) {
        try {
            block()
            yield()
        } catch (ex: CancellationException) {
            println("coroutine on ${currentThread().name} cancelled")
        } catch (ex: Exception) {
            println("${currentThread().name} failed with {$ex}. Retrying...")
            ex.printStackTrace()
        }
    }

    println("coroutine on ${currentThread().name} exiting")
}