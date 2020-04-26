package com.paulmethfessel.cad.util

import java.text.SimpleDateFormat
import java.util.*


object Logger {
    fun debug(message: String) {
        val timeStamp = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Date())
        println("$timeStamp: $message")
    }

    fun error(message: String) {
        debug(message)
    }
}