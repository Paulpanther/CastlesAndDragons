package server

import generator.GridGenerator
import model.Grid
import solver.GridSolver
import util.DelayTimer
import util.ListExt.without

const val WIDTH = 5
const val HEIGHT = 3
const val GAME_DELAY = 8000

class Game(private val players: List<Client>): ClientListener() {

    private var running = false
    private val playersInGame = players.toMutableList()
    private val delayTimer = DelayTimer("gameDelay", GAME_DELAY, this::generateGrid)

    init {
        players.forEach { it.grid = Grid(WIDTH, HEIGHT) }
        sendTo(players, Response.startGame(WIDTH, HEIGHT, GAME_DELAY))
        delayTimer.restart()
        println("Start Game")
    }

    private fun generateGrid() {
        val grid = GridGenerator().generateRandom(WIDTH, HEIGHT)
        players.forEach {
            it.grid = grid
            it.send(Response.setGrid(it, grid))
        }
        running = true
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
                Server.closeGame(players)
            }
        }
    }

    override fun onMessage(client: Client, message: Message) {
        if (client in players) {
            if (message.type == MessageType.MOVE) {
                onMove(client, message as MoveMessage)
            } else if (message.type == MessageType.FINISHED) {
                onFinished(client)
            }
        }
    }

    override fun onLeave(client: Client) {
        if (client in players) {
            sendTo(players.without(client), Response.left(client))
            delayTimer.stop()
            stopListening()
            Server.closeGame(players)
        }
    }
}