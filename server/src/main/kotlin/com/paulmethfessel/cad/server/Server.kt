package com.paulmethfessel.cad.server

import com.paulmethfessel.cad.generator.GridPool
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory

private val tag = MarkerFactory.getMarker("Server")

object Server: ClientListener(false) {

    val log = LoggerFactory.getLogger(Server::class.java)

    val config: Config get() = _config
    private lateinit var _config: Config

    private val rooms = mutableListOf<WaitingRoom>()
    private val games = mutableListOf<Game>()

    fun start(config: Config) {
        _config = config
        GridPool.run()
        Clients.addListener(this)
        Clients.runServer()
    }

    override fun onMessage(client: Client, message: Message) {
        if (message.type == MessageType.ENTERROOM) {
            val id = (message as RoomIdMessage).id
            if (id == null || rooms.find { it.roomId == id } == null) {
                createNewWaitingRoom(client)
            } else {
                val roomWithId = rooms.find { it.roomId == id }
                if (roomWithId != null) {
                    val success = roomWithId.add(client)
                    if (!success) {
                        createNewWaitingRoom(client)
                    }
                }
            }
        }
    }

    private fun createNewWaitingRoom(client: Client) {
        val allIds = rooms.map { it.roomId }
        val newId = IdGenerator.generateRandomId(allIds)
        val newRoom = WaitingRoom(newId)
        rooms += newRoom
        newRoom.add(client)
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
        log.info(tag, "Open Rooms: WaitingRooms=${rooms.size}, Games=${games.size}")
    }

    fun <T: Room> switchRoom(old: Room, next: (roomId: String, players: MutableList<Client>) -> T): T {
        val nextInstance = next(old.roomId, old.players)
        closeRoom(old)
        openRoom(nextInstance)
        return nextInstance
    }

    fun reset() {
        rooms.toList().forEach { closeRoom(it) }
        games.toList().forEach { closeRoom(it) }
    }

    fun close() {
        log.info(tag, "Closing Server")
        GridPool.close()
    }
}