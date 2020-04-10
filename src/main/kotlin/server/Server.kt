package server

object Server {

    fun start() {
        WaitingRoom()
        Clients.runServer()
    }

    fun closeGame(players: List<Client>) {

    }

    fun startGame(players: List<Client>) {

    }
}