package server

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

object Clients : WebSocketListener() {

    private val clients = mutableListOf<Client>()
    private val listeners = mutableListOf<ClientListener>()

    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        super.onOpen(webSocket, response)
        if (webSocket != null) {
            val existing = clients.find { it.socket == webSocket }
            if (existing == null) {
                val newClient = Client(webSocket)
                clients.add(newClient)
                listeners.forEach { it.onJoined(newClient) }
            }
        }
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        super.onMessage(webSocket, text)
    }

    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        super.onClosed(webSocket, code, reason)
        val client = clientFromSocket(webSocket)
        if (client != null) {
            listeners.forEach { it.onLeave(client) }
        }
        error("Socket closed")
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        super.onFailure(webSocket, t, response)
        val client = clientFromSocket(webSocket)
        if (client != null) {
            listeners.forEach { it.onLeave(client) }
        }
        error("Socket failure")
    }

    fun addListener(listener: ClientListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ClientListener) {
        listeners.remove(listener)
    }

    private fun clientFromSocket(socket: WebSocket?) = clients.find { it.socket == socket }
}

class Client(val socket: WebSocket)

open class ClientListener {

    init {
        Clients.addListener(this)
    }

    fun onJoined(client: Client) {}
    fun onUpdateName(client: Client, name: String) {}
    fun onLeave(client: Client) {}

    fun stopListening() {
        Clients.removeListener(this)
    }
}