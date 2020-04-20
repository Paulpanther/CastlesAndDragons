package com.paulmethfessel.cad.server

import com.paulmethfessel.cad.util.DelayTimer

class WaitingRoom(private val players: MutableList<Client> = mutableListOf()) : ClientListener() {

    private val timer = DelayTimer("gameStarts", Server.config.startGameDelay,
            this::startGame,
            { sendTo(players, Response.gameStartsIn(Server.config.startGameDelay)) },
            { sendTo(players, Response.gameStartStopped()) })

    override fun onJoined(client: Client) {
        if (players.size + 1 <= Server.config.playerCount) {
            client.send(Response.nameAndId(client))
            players.add(client)
            sendPlayersList()
            println("Player joined: ${client.id}, ${client.name}")

            if (players.size >= Server.config.playerCount && !timer.isRunning) {
                timer.restart()
            }
        } else {
            client.send(Response.ERROR_ROOM_FULL)
        }
    }

    override fun onMessage(client: Client, message: Message) {
        if (client in players) {
            if (message.type == MessageType.NAME) {
                onNameMessage(client, message as NameMessage)
            }
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

    override fun onLeave(client: Client) {
        println("left: ${client.name}")
        if (client in players) {
            players.remove(client)
            timer.stop()
            sendPlayersList()
        }
    }

    private fun startGame() {
        stopListening()
        Server.startGame(players)
    }

    private fun sendPlayersList() {
        sendTo(players, Response.setPlayers(players.map { it.id.toString() }, players.map(Client::name)))
    }
}