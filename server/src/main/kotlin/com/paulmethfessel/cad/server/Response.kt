package com.paulmethfessel.cad.server

import com.paulmethfessel.cad.model.Grid

object Response {

    val ERROR_INVALID_NAME = error("invalidName")
    val ERROR_ROOM_FULL = error("room_full")

    fun gameStartsIn(delay: Int)
            = join(type("gameStartsIn"), v("delay", delay.toString()))
    fun gameStartStopped() = type("gameStartStopped")

    fun nameAndId(client: Client) = join(type("nameAndId"), client(client))
    fun setPlayers(ids: List<String>, names: List<String>)
            = join(type("setPlayers"), v("ids", *ids.toTypedArray()), v("names", *names.toTypedArray()))
    fun left(client: Client) = join(type("left"), client(client))

    fun joinedWaitingRoom(roomId: String) = join(type("joinedWaiting"), v("id", roomId))

    fun startGame(width: Int, height: Int, delay: Int)
            = join(type("gameStart"), v("size", width.toString(), height.toString()), v("delay", delay.toString()))
    fun setGrid(client: Client, grid: Grid)
            = join(type("setGrid"), client(client), v("grid", grid.toShortString()))

    fun finished(client: Client, delay: Int)
            = join(type("finished"), client(client), v("delay", delay.toString()))
    fun notFinished(client: Client) = join(type("notFinished"), client(client))
    fun won(client: Client, delay: Int) = join(type("won"), client(client), v("delay", delay.toString()))

    private fun error(name: String) = join(type("error"), v("error", name))
    private fun client(client: Client) = v("client", client.id.toString(), client.name, client.level.toString())
    private fun type(type: String) = v("type", type)
    private fun v(key: String, vararg value: String) = "$key=${value.joinToString(",")}"
    private fun join(vararg args: String) = args.joinToString(";")
}