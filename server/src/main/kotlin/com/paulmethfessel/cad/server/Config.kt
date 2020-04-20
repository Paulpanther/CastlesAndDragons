package com.paulmethfessel.cad.server

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class Config(parser: ArgParser) {
    val gridWidth by parser.storing("the width of the grid") { toInt() }.default(5)
    val gridHeight by parser.storing("the height of the grid") { toInt() }.default(3)

    private val _startGameDelay by parser.storing(
            "--start-game-delay",
            help = "the delay in the waiting room before a game starts") { toInt() }.default(10000)
    private val _showGridDelay by parser.storing(
            "--show-grid-delay",
            help = "the delay in game before the task is shown") { toInt() }.default(3000)

    private val _playerCount by parser.storing(
            "--player-count",
            help = "the amount of players in each game") { toInt() }.default(4)

    val startGameDelay: Int
    val showGridDelay: Int

    val playerCount: Int

    val port by parser.storing("the port for the server") { toInt() }.default(6789)

    val debugStartWithGame by parser.flagging("skip the waiting room")

    init {
        if (debugStartWithGame) {
            startGameDelay = 0
            showGridDelay = 0
            playerCount = 1
        } else {
            startGameDelay = _startGameDelay
            showGridDelay = _showGridDelay
            playerCount = _playerCount
        }
    }
}
