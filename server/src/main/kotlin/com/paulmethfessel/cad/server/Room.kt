package com.paulmethfessel.cad.server

typealias RoomClass<T> = (players: MutableList<Client>) -> T;

abstract class Room(val players: MutableList<Client> = mutableListOf()): ClientListener() {

    override fun onMessage(client: Client, message: Message) {
        super.onMessage(client, message)
        if (client in players) {
            onPlayerMessage(client, message)
        }
    }

    override fun onLeave(client: Client) {
        super.onLeave(client)
        if (client in players) {
            players.remove(client)
            onPlayerLeave(client)
        }
    }

    protected fun sendToPlayers(response: String) {
        sendTo(players, response)
    }

    protected fun close() {
        Server.closeRoom(this)
    }

    protected fun <T: Room> switchTo(next: RoomClass<T>) = Server.switchRoom(this, next)

    abstract fun onPlayerMessage(client: Client, message: Message)
    abstract fun onPlayerLeave(client: Client)
}