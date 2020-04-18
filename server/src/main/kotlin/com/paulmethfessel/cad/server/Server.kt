package com.paulmethfessel.cad.server

object Server {

    private val rooms = mutableListOf<WaitingRoom>()
    private val games = mutableListOf<Game>()

    fun start() {
        rooms += WaitingRoom()
        Clients.runServer()
    }

    fun closeGame(players: List<Client>) {
        rooms += WaitingRoom(players.toMutableList())
    }

    fun startGame(players: List<Client>) {
        games += Game(players)
    }

    fun reset() {
        rooms.forEach { it.stopListening() }
        games.forEach { it.stopListening() }
        rooms += WaitingRoom()
    }
}