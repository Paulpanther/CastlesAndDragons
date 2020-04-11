package server

import util.ListExt.without
import java.util.*
import kotlin.concurrent.schedule

const val MAX_PLAYERS = 4
const val GAME_STARTS_TIME = 10000L

class WaitingRoom(private val players: MutableList<Client> = mutableListOf()) : ClientListener() {

    private var gameStartsTimer = Timer("gameStarts", true)
    private var timerIsRunning = false

    override fun onJoined(client: Client) {
        if (players.size + 1 <= MAX_PLAYERS) {
            client.send(Response.nameAndId(client))
            players.add(client)
            sendPlayersList()
            println("Player joined: ${client.id}, ${client.name}")

            if (players.size >= MAX_PLAYERS && !timerIsRunning) {
                restartGameTimer()
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
            stopGameTimer()
            players.remove(client)
            sendPlayersList()
        }
    }

    private fun restartGameTimer() {
        if (timerIsRunning) {
            gameStartsTimer.cancel()
        }

        timerIsRunning = true
        sendTo(players, Response.gameStartsIn(GAME_STARTS_TIME))
        gameStartsTimer.schedule(GAME_STARTS_TIME) {
            startGame()
        }
    }

    private fun stopGameTimer() {
        if (timerIsRunning) {
            gameStartsTimer.cancel()
            timerIsRunning = false
            sendTo(players, Response.gameStartStopped())
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