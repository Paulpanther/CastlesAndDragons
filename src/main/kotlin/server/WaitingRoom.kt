package server

import util.ListExt.without

class WaitingRoom(private val players: MutableList<Client> = mutableListOf()) : ClientListener() {

    override fun onJoined(client: Client) {
        sendTo(players.without(client), Response.joined(client))
        client.send(Response.nameAndId(client))
        players.add(client)
        println("Player joined: ${client.id}, ${client.name}")
    }

    override fun onMessage(client: Client, message: Message) {
        if (client in players) {
            if (message.type == MessageType.NAME) {
                val name = (message as NameMessage).name
                if (NameGenerator.validName(name)) {
                    client.name = name
                    client.send(Response.nameAndId(client))
                    sendTo(players.without(client), Response.changedName(client))
                } else {
                    client.send(Response.ERROR_INVALID_NAME)
                }
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