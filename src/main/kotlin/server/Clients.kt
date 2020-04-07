package server

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress

const val PORT = 6789

object Clients: WebSocketServer(InetSocketAddress(PORT)) {

    private val clients = mutableListOf<Client>()
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
                val newClient = Client(conn)
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

    private fun notifyListeners(conn: WebSocket?, run: (ClientListener, Client) -> Unit) {
        clientFromSocket(conn)?.let { client -> listeners.forEach { l -> run(l, client) } }
    }

    private fun clientFromSocket(socket: WebSocket?) = clients.find { it.socket == socket }
}

class Client(val socket: WebSocket)

open class ClientListener {

    init {
        Clients.addListener(this)
    }

    fun onJoined(client: Client) {}
    fun onMessage(client: Client, message: Message) {}
    fun onLeave(client: Client) {}

    fun stopListening() {
        Clients.removeListener(this)
    }
}