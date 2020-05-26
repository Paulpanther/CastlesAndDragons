package com.paulmethfessel.cad.server

import com.paulmethfessel.cad.util.DelayTimer
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class WaitingRoom(
        roomId: String,
        players: MutableList<Client> = mutableListOf()
) : Room(roomId, players) {

    private val timer = DelayTimer("gameStarts", Server.config.startGameDelay,
            { switchTo(::Game) },
            { sendToPlayers(Response.gameStartsIn(Server.config.startGameDelay)) },
            { sendToPlayers(Response.gameStartStopped()) })

    private val joinLock = ReentrantLock()

    init {
        sendToPlayers(Response.joinedWaitingRoom(roomId))
    }

    fun add(client: Client): Boolean {
        joinLock.withLock {
            return if (players.size + 1 <= Server.config.playerCount) {
                client.roomId = roomId
                client.send(Response.joinedWaitingRoom(roomId))
                client.send(Response.nameAndId(client))
                players.add(client)
                sendPlayersList()

                if (players.size >= Server.config.playerCount && !timer.isRunning) {
                    timer.restart()
                }
                true
            } else {
                client.send(Response.ERROR_ROOM_FULL)
                false
            }
        }
    }

    override fun onPlayerMessage(client: Client, message: Message) {
        if (message.type == MessageType.NAME) {
            onNameMessage(client, message as NameMessage)
        }
    }

    private fun onNameMessage(client: Client, message: NameMessage) {
        if (NameGenerator.validName(players.map(Client::name), message.name)) {
            client.name = message.name
            client.send(Response.nameAndId(client))
            sendPlayersList()
        } else {
            client.send(Response.ERROR_INVALID_NAME)
        }
    }

    override fun onPlayerLeave(client: Client) {
        players.remove(client)
        timer.stop()
        sendPlayersList()
        if (players.isEmpty()) {
            close()
        }
    }

    private fun sendPlayersList() {
        sendToPlayers(Response.setPlayers(players.map { it.id.toString() }, players.map(Client::name)))
    }
}