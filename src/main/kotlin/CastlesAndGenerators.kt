import generator.RecursiveGenerator
import server.Server

fun main() {
//    Server.start()
    val grid = RecursiveGenerator(5, 3).generate()
    println(grid.toShortString())
    println(grid.heroPos)
}