package com.paulmethfessel.cad.util

import com.paulmethfessel.cad.server.Server
import org.slf4j.MarkerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit


private val tag = MarkerFactory.getMarker("ExecutorService")

fun ExecutorService.shutdownSafelyNow(waitMillis: Long = 1000) {
    try {
        shutdown()
        awaitTermination(waitMillis, TimeUnit.MILLISECONDS)
    } catch (e: InterruptedException) {
        Server.log.error(tag, "Interrupted running Tasks")
    } finally {
        shutdownNow()
    }

}