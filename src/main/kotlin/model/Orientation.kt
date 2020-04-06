package model

enum class Orientation {
    NORTH, WEST, SOUTH, EAST
}

data class OrientationMap<T>(
        private val north: T,
        private val west: T,
        private val south: T,
        private val east: T): Iterable<T> {

    operator fun get(orientation: Orientation): T {
        return when(orientation) {
            Orientation.NORTH -> north
            Orientation.WEST -> west
            Orientation.SOUTH -> south
            Orientation.EAST -> east
        }
    }

    override fun iterator(): Iterator<T> {
        return listOf(north, west, south, east).iterator()
    }
}

class Orientations<T> {
    var north: T? = null
    var west: T? = null
    var south: T? = null
    var east: T? = null

    fun build(): OrientationMap<T> {
        if (north == null || west == null || south == null || east == null) {
            throw IllegalStateException("Building not finished, some orientations are not set")
        }
        return OrientationMap(north!!, west!!, south!!, east!!)
    }
}

fun Orientation.toLocal(up: Orientation) = Orientation.values()[(this.ordinal + up.ordinal) % 4]
fun Orientation.toWorld(up: Orientation) = Orientation.values()[(this.ordinal - up.ordinal) % 4]
fun Orientation.opposite() = Orientation.values()[(this.ordinal + 2) % 4]

val shortOrientations = mapOf(
        Orientation.NORTH to "n",
        Orientation.WEST to "w",
        Orientation.SOUTH to "s",
        Orientation.EAST to "e"
)

fun Orientation.toShortString() = shortOrientations[this]?: "?"

fun Orientation.flipXAxis(): Orientation {
    return if (this == Orientation.NORTH || this == Orientation.SOUTH)
        this
    else
        this.opposite()
}

fun Orientation.flipYAxis(): Orientation {
    return if (this == Orientation.WEST || this == Orientation.EAST)
        this
    else
        this.opposite()
}

