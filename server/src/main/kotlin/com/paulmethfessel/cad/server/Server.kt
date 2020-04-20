package com.paulmethfessel.cad.server

object Server {

    private lateinit var _config: Config
    val config: Config get() = _config

    private val rooms = mutableListOf<WaitingRoom>()
    private val games = mutableListOf<Game>()

    fun start(config: Config) {
        _config = config
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