package com.paulmethfessel.cad.util

object ListExt {
    fun <T> List<T>.without(elem: T) = this.filter { it != elem }

    fun <K, V> Map<K ,V>.reverse(): Map<V, K> {
        val reversed = mutableMapOf<V, K>()
        this.forEach { (k, v) -> reversed[v] = k }
        return reversed
    }
}