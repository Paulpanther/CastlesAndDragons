package com.paulmethfessel.cad

import com.paulmethfessel.cad.server.Config
import com.paulmethfessel.cad.server.Server

fun main(args: Array<String>) {
    println("Running")
    if (args.contains("--debugStartWithGame")) {
        debugStartWithGame()
    } else {
        Server.start()
    }
}

private fun debugStartWithGame() {
    Config.playerCount = 1
    Config.showGridDelay = 0
    Config.startGameDelay = 0
    Server.start()
}