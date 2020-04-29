package com.paulmethfessel.cad.model

import java.lang.IllegalArgumentException

data class Pos(var x: Int, var y: Int) {

    companion object {
        fun fromIndex(i: Int, width: Int): Pos {
            val x = i % width
            val y = i / width
            return Pos(x, y)
        }
    }

    operator fun plus(pos: Pos) = Pos(this.x + pos.x, this.y + pos.y)
    operator fun minus(pos: Pos) = Pos(this.x - pos.x, this.y - pos.y)

    operator fun plus(n: Int) = Pos(this.x + n, this.y + n)
    operator fun minus(n: Int) = Pos(this.x - n, this.y - n)
    operator fun times(n: Int) = Pos(this.x * n, this.y * n)
    operator fun div(n: Int) = Pos(this.x / n, this.y / n)

    fun toIndex(width: Int) = y * width + x

    /**
     * In which orientation do you have to move to come from this to other.
     * ```
     * Pos.whichMoveInOrientation(Pos.moveInOrientation(Orientation)) == Orientation
     * ```
     */
    fun whichMoveOrientation(other: Pos): Orientation {
        return when {
            other.x == x && other.y < y -> Orientation.NORTH
            other.x == x && other.y > y -> Orientation.SOUTH
            other.y == y && other.x < x -> Orientation.WEST
            other.y == y && other.x > x -> Orientation.EAST
            else -> throw IllegalArgumentException("Pos is not on same axis or has same position")
        }
    }

    fun moveInOrientation(orientation: Orientation): Pos {
        return when (orientation) {
            Orientation.NORTH -> Pos(x, y - 1)
            Orientation.WEST -> Pos(x - 1, y)
            Orientation.SOUTH -> Pos(x, y + 1)
            Orientation.EAST -> Pos(x + 1, y)
        }
    }

    override fun toString() = "($x, $y)"

    fun clone() = Pos(x, y)
}

