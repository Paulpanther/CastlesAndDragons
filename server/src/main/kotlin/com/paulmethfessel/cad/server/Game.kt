package com.paulmethfessel.cad.server

import com.paulmethfessel.cad.generator.GridPool
import com.paulmethfessel.cad.model.Grid
import com.paulmethfessel.cad.solver.GridSolver
import com.paulmethfessel.cad.util.DelayTimer
import com.paulmethfessel.cad.util.ListExt.without
import org.slf4j.MarkerFactory
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private val tag = MarkerFactory.getMarker("GAME")

class Game(players: MutableList<Client>): Room(players) {

    private var running = false
    private var showGridDelayFinished = false
    private val playersInGame = players.toMutableList()
    private val delayTimer = DelayTimer("gameDelay", Server.config.showGridDelay, this::showGrid)

    private lateinit var chosenGrid: Grid

    private val moveLock = ReentrantLock()
    private val finishLock = ReentrantLock()

    init {
        val grid = GridPool.getNewGrid()
        if (grid != null) {
            chosenGrid = grid
        } else {
            GridPool.notifyOnNewGrid {
                chosenGrid = it
                if (showGridDelayFinished) {
                    // Delay has already passed, so call show Grid again
                    showGrid()
                }
            }
        }

        players.forEach { it.grid = Grid(Server.config.gridWidth, Server.config.gridHeight) }
        sendTo(players, Response.startGame(Server.config.gridWidth, Server.config.gridHeight, Server.config.showGridDelay))

        delayTimer.restart()
    }

    private fun showGrid() {
        showGridDelayFinished = true
        if (::chosenGrid.isInitialized) {
            players.forEach {
                it.grid = chosenGrid.clone()
                sendToPlayers(Response.setGrid(it, it.grid))
            }
            running = true
        }
    }

    // TODO move places on wrong
    private fun onMove(client: Client, move: MoveMessage) {
        moveLock.withLock {
            if (running && client in playersInGame) {

                // Remove item from position
                if (move.from != null) {
                    client.grid.setEmpty(move.from.x, move.from.y)
                }

                // Put item to some position in some orientation
                if (move.to != null && move.up != null) {
                    if (client.grid.canBeSet(move.item)) {
                        client.grid.setItem(move.to, move.item, move.up)
                    } else {
                        Server.log.error(tag, "Client ${client.id} cannot put item ${move.item} into grid")
                    }
                }

                // Send new grid to all players
                sendTo(players, Response.setGrid(client, client.grid))
            }
        }
    }

    private fun onFinished(client: Client) {
        finishLock.withLock {
            if (running && client in playersInGame) {
                val isFinished = GridSolver.isFinished(client.grid, client.level)

                if (!isFinished) {
                    // Player has wrong solution
                    playersInGame.remove(client)
                    sendTo(players, Response.notFinished(client))
                } else {
                    // Player has right solution

                    client.level++
                    if (client.level >= 3) {
                        // Player has won
                        sendTo(players, Response.won(client))
                        players.forEach { it.reset() }

                        running = false
                        switchTo(::WaitingRoom)
                    } else {
                        sendTo(players, Response.finished(client))

                        // Start new Game
                        running = false
                        switchTo(::Game)
                    }
                }
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
        switchTo(::WaitingRoom)
    }
}