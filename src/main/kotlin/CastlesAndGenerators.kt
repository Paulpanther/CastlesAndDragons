import util.Random

fun main(args: Array<String>) {
    for (i in 0 until 100) {
        println(Random.shuffle(listOf(1, 2, 3, 4, 5)))
    }
}