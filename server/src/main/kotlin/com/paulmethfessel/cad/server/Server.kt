package com.paulmethfessel.cad.server

import org.slf4j.LoggerFactory

object Server {

    val log = LoggerFactory.getLogger(Server::class.java)

    val config: Config get() = _config
    private lateinit var _config: Config

    private val rooms = mutableListOf<WaitingRoom>()
    private val games = mutableListOf<Game>()

    fun start(config: Config) {
        _config = config
        rooms += WaitingRoom()
        Clients.runServer()
    }

    fun closeRoom(room: Room) {
        room.stopListening()
        if (room is WaitingRoom) {
            rooms -= room
        } else if (room is Game) {
            games -= room
        }
    }

    fun openRoom(room: Room) {
        if (room is WaitingRoom) {
            rooms += room
        } else if (room is Game) {
            games += games
        }
    }

    fun <T: Room> switchRoom(old: Room, next: (players: MutableList<Client>) -> T): T {
        val nextInstance = next(old.players)
        closeRoom(old)
        openRoom(nextInstance)
        return nextInstance
    }

    fun reset() {
        rooms.forEach { closeRoom(it) }
        games.forEach { closeRoom(it) }
        openRoom(WaitingRoom())
    }
}