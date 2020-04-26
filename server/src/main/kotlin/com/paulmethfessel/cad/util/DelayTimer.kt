package com.paulmethfessel.cad.util

import com.paulmethfessel.cad.server.Server
import java.util.*
import kotlin.concurrent.schedule

class DelayTimer(
        private val name: String,
        private val delay: Int,
        private val onDelay: () -> Unit,
        private val onStart: () -> Unit = {},
        private val onStop: () -> Unit = {}) {

    private var timer: Timer? = null
    private var running = false
    val isRunning get() = running

    fun restart() {
        if (isRunning) {
            timer?.cancel()
        }

        running = true
        onStart()
        timer = Timer(name, true)
        timer?.schedule(delay.toLong()) {
            Logger.debug("Timer has run")
            onDelay()
        }
    }

    fun stop() {
        if (running) {
            running = false
            timer?.cancel()
            onStop()
        }
    }
}