package server

import util.ListExt.without

class WaitingRoom(private val players: MutableList<Client> = mutableListOf()) : ClientListener() {

    override fun onJoined(client: Client) {
        sendTo(players.without(client), Response.joined(client))
        players.add(client)
    }

    override fun onMessage(client: Client, message: Message) {
        if (client in players) {
            if (message.type == MessageType.NAME) {
                sendTo(players.without(client), Response.changedName(client))
            }
        }
    }

    override fun onLeave(client: Client) {
        if (client in players) {
            sendTo(players.without(client), Response.changedName(client))
            players.remove(client)
        }
    }

    private fun startGame() {
        Game(players)
        stopListening()
    }
}