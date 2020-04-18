package com.paulmethfessel.cad.util

import java.util.*
import kotlin.concurrent.schedule

class DelayTimer(
        private val name: String,
        private val delay: Int,
        private val onDelay: () -> Unit,
        private val onStart: () -> Unit = {},
        private val onStop: () -> Unit = {}) {

    private val timer = Timer(name, true)
    private var running = false
    val isRunning get() = running

    fun restart() {
        if (isRunning) {
            timer.cancel()
        }

        running = true
        onStart()
        timer.schedule(delay.toLong()) { onDelay() }
    }

    fun stop() {
        if (running) {
            running = false
            timer.cancel()
            onStop()
        }
    }
}