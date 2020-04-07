import generator.GridGenerator
import server.Clients
import server.Server

fun main(args: Array<String>) {
//    println(GridGenerator().generateRandom(15, 5))
    Clients.runServer()
}