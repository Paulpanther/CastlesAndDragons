package server

import model.Grid
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress

const val PORT = 6789

object Clients: WebSocketServer(InetSocketAddress(PORT)) {

    val clients = mutableListOf<Client>()
    private val listeners = mutableListOf<ClientListener>()

    fun runServer() {
        start()
        println("Websocket server running on port ${this.port}")

        Runtime.getRuntime().addShutdownHook(Thread {
            println("Shutdown")
            stop(1000)
        })
    }


    override fun onStart() {}

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        if (conn != null) {
            val existing = clients.find { it.socket == conn }
            if (existing == null) {
                val newClient = Client(IdGenerator.generateId(allIds()),
                        conn, NameGenerator.generateName(allNames()))
                clients.add(newClient)
                listeners.forEach { it.onJoined(newClient) }
                println("New Connection: ${conn.remoteSocketAddress.address.hostAddress}")
            }
        }
    }

    override fun onMessage(conn: WebSocket?, str: String?) {
        val parsed = Message.parse(str)
        notifyListeners(conn) { l, c -> l.onMessage(c, parsed) }
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        notifyListeners(conn) { l, c -> l.onLeave(c) }
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        notifyListeners(conn) { l, c -> l.onLeave(c) }
        println("Error")
    }

    fun addListener(listener: ClientListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ClientListener) {
        listeners.remove(listener)
    }

    private fun allNames() = clients.map { it.name }
    private fun allIds() = clients.map { it.id }

    private fun notifyListeners(conn: WebSocket?, run: (ClientListener, Client) -> Unit) {
        clientFromSocket(conn)?.let { client -> listeners.forEach { l -> run(l, client) } }
    }

    private fun clientFromSocket(socket: WebSocket?) = clients.find { it.socket == socket }
}

class Client(
        val id: Int,
        val socket: WebSocket,
        var name: String,
        var grid: Grid? = null) {

    fun send(text: String) {
        socket.send(text)
    }
}

open class ClientListener {

    init {
        Clients.addListener(this)
    }

    open fun onJoined(client: Client) {}
    open fun onMessage(client: Client, message: Message) {}
    open fun onLeave(client: Client) {}

    fun stopListening() {
        Clients.removeListener(this)
    }

    fun broadcast(text: String) {
        Clients.broadcast(text)
    }

    protected fun sendToSome(textMapper: (Client) -> String?) {
        for (client in Clients.clients) {
            val text = textMapper(client)
            text?.let { client.send(text) }
        }
    }

    protected fun sendTo(clients: List<Client>, text: String) {
        sendToSome { if (it in clients) text else null }
    }
}