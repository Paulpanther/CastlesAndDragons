package server

import model.Grid

object Response {

    fun left(client: Client) = join(type("left"), client(client))
    fun joined(client: Client) = join(type("joined"), client(client))
    fun changedName(client: Client) = join(type("changedName"), client(client))
    fun startGame(width: Int, height: Int)
            = join(type("startGame"), v("size", width.toString(), height.toString()))
    fun setGrid(client: Client, grid: Grid)
            = join(type("setGrid"), client(client), v("grid", grid.toShortString()))
    fun finished(client: Client) = join(type("finished"), client(client))

    private fun client(client: Client) = v("client", client.id.toString(), client.name)
    private fun type(type: String) = v("type", type)
    private fun v(key: String, vararg value: String) = "$key=${value.joinToString(",")}"
    private fun join(vararg args: String) = args.joinToString(";")
}