package server

import generator.GridGenerator
import model.Grid
import util.ListExt.without

const val WIDTH = 15
const val HEIGHT = 5

class Game(private val players: List<Client>): ClientListener() {

    init {
        players.forEach { it.grid = Grid(WIDTH, HEIGHT) }
        sendTo(players, Response.startGame(WIDTH, HEIGHT))
    }

    fun generateGrid() {
        val grid = GridGenerator().generateRandom(WIDTH, HEIGHT)
        players.forEach {
            it.grid = grid
            it.send(Response.setGrid(it, grid))
        }
    }

    override fun onLeave(client: Client) {
        if (client in players) {
            WaitingRoom(players.filter { it != client }.toMutableList())
            sendTo(players.without(client), Response.left(client))
            stopListening()
        }
    }
}