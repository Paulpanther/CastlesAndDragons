package main.kotlin.model

enum class Orientation {
    NORTH, WEST, SOUTH, EAST
}

data class OrientationMap<T>(
        private val north: T,
        private val west: T,
        private val south: T,
        private val east: T) {

    operator fun get(orientation: Orientation): T {
        return when(orientation) {
            Orientation.NORTH -> north
            Orientation.WEST -> west
            Orientation.SOUTH -> south
            Orientation.EAST -> east
        }
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

object OrientationTransformer {
    fun worldToLocal(world: Orientation, up: Orientation): Orientation {
        return Orientation.values()[(world.ordinal + up.ordinal) % 4]
    }

    fun localToWorld(local: Orientation, up: Orientation): Orientation {
        return Orientation.values()[(local.ordinal - up.ordinal) % 4]
    }

    fun opposite(orientation: Orientation): Orientation {
        return Orientation.values()[(orientation.ordinal + 2) % 4]
    }
}
