package com.paulmethfessel.cad.util

object Random {
    fun <T> shuffle(list: List<T>): List<T> {
        return multipleFrom(list.size, list)
    }

    fun multipleFrom(amount: Int, range: IntRange): List<Int> {
        return multipleFrom(amount, range.toList())
    }

    fun <T> multipleFrom(amount: Int, list: List<T>): List<T> {
        if (amount > list.size) {
            throw IllegalArgumentException("Cannot generate more numbers than in range")
        }

        val possibleValuesIndices = list.indices.toMutableList()
        return (0 until amount).map {
            val randomI = from(possibleValuesIndices)
            possibleValuesIndices.remove(randomI)
            list[randomI]
        }
    }

    fun <T> from(list: List<T>): T {
        return list[(Math.random() * list.size).toInt()]
    }

    fun bool() = Math.random() < 0.5

    fun between(range: IntRange): Int {
        return from(range.toList())
    }
}
