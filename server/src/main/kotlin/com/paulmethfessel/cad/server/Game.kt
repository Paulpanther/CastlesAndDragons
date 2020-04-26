package com.paulmethfessel.cad.server

import com.paulmethfessel.cad.generator.RecursiveGenerator
import com.paulmethfessel.cad.model.Grid
import com.paulmethfessel.cad.solver.GridSolver
import com.paulmethfessel.cad.util.DelayTimer
import com.paulmethfessel.cad.util.ListExt.without
import com.paulmethfessel.cad.util.Logger
import org.slf4j.LoggerFactory

class Game(players: MutableList<Client>): Room(players) {

    private var running = false
    private val playersInGame = players.toMutableList()
    private val delayTimer = DelayTimer("gameDelay", Server.config.showGridDelay, this::generateGrid)

    init {
        players.forEach { it.grid = Grid(Server.config.gridWidth, Server.config.gridHeight) }
        sendTo(players, Response.startGame(Server.config.gridWidth, Server.config.gridHeight, Server.config.showGridDelay))

        Logger.debug("Start Timer in Game")
        delayTimer.restart()
        Logger.debug("Start Game")
    }

    private fun generateGrid() {
        Logger.debug("Start finished")
        val grid = RecursiveGenerator(Server.config.gridWidth, Server.config.gridHeight).generate()
        Logger.debug("Generation finished")
        players.forEach {
            it.grid = grid
            it.send(Response.setGrid(it, grid))
        }
        running = true
        Logger.debug("Sending finished")
    }

    private fun onMove(client: Client, move: MoveMessage) {
        if (running && client in playersInGame) {
            if (move.from != null) {
                println("FROM ${move.from.x}")
                client.grid.setEmpty(move.from.x, move.from.y)
            }
            if (move.to != null && move.up != null) {
                println("TO ${move.to.x}, ${move.item}:${move.up}")
                client.grid.setItem(move.to, move.item, move.up)
            }
            sendTo(players, Response.setGrid(client, client.grid))
        }
    }

    private fun onFinished(client: Client) {
        if (running && client in playersInGame) {
            val isFinished = GridSolver.isFinished(client.grid, client.level)
            if (!isFinished) {
                playersInGame.remove(client)
                sendTo(players, Response.notFinished(client))
            } else {
                client.level++
                if (client.level >= 3) {
                    sendTo(players, Response.won(client))
                    players.forEach { it.reset() }
                } else {
                    sendTo(players, Response.finished(client))
                }
                switchTo(::Game)
            }
        }
    }

    override fun onPlayerMessage(client: Client, message: Message) {
        if (message.type == MessageType.MOVE) {
            onMove(client, message as MoveMessage)
        } else if (message.type == MessageType.FINISHED) {
            onFinished(client)
        }
    }

    override fun onPlayerLeave(client: Client) {
        sendTo(players.without(client), Response.left(client))
        delayTimer.stop()
        stopListening()
        close()
    }
}