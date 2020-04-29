package com.paulmethfessel.cad.server

import com.paulmethfessel.cad.model.Grid
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import org.slf4j.MarkerFactory
import java.lang.Exception
import java.net.InetSocketAddress
import kotlin.system.exitProcess

private val tag = MarkerFactory.getMarker("CONNECTIONS")

object Clients: WebSocketServer(InetSocketAddress(Server.config.port)) {

    val clients = mutableListOf<Client>()
    private val listeners = mutableListOf<ClientListener>()

    fun runServer() {
        isReuseAddr = true
        start()
        Server.log.info(tag, "Websocket server running on port ${this.port}")

        Runtime.getRuntime().addShutdownHook(Thread {
            Server.close()
        })

        try {
            run@ do {
                when (readLine()) {
                    "exit" -> break@run
                    "clear" -> clients.clear()
                    "restart" -> restart()
                }
            } while (true)
        } catch (e: InterruptedException) {}
        finally {
            shutdown()
        }
    }

    private fun restart() {
        clients.clear()
        Server.reset()
        Server.log.info(tag, "Restarted")
    }

    private fun shutdown() {
        Server.log.info(tag, "Shutting down")

        clients.clear()
        stop(500)
        exitProcess(0)
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
            }
        }
    }

    override fun onMessage(conn: WebSocket?, str: String?) {
        Server.log.debug(tag, "in: $str")
        val parsed = Message.parse(str)
        if (parsed.type == MessageType.RESTART) {
            restart()
        }
        notifyListeners(conn) { l, c -> l.onMessage(c, parsed) }
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        if (remote) {
            notifyListeners(conn) { l, c -> l.onLeave(c) }
        }
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        notifyListeners(conn) { l, c -> l.onLeave(c) }
        Server.log.error(tag, "in onError", ex)
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
        val copiedListeners = listeners.toList()
        clientFromSocket(conn)?.let { client -> copiedListeners.forEach { l -> run(l, client) } }
    }

    private fun clientFromSocket(socket: WebSocket?) = clients.find { it.socket == socket }
}

class Client(
        val id: Int,
        val socket: WebSocket,
        var name: String,
        var level: Int = 0) {

    lateinit var grid: Grid

    fun send(text: String) {
        if (socket.isOpen) {
            Server.log.info(tag, "out: $text")
            socket.send(text)
        } else {
            Server.log.error("Connection not open. Did not send: $text")
        }
    }

    fun reset() {
        level = 0
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