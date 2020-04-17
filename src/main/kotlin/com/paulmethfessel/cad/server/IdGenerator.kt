package com.paulmethfessel.cad.server

object IdGenerator {

    fun generateId(idsInUse: List<Int>): Int {
        var id = 0
        while (id in idsInUse) { id++ }
        return id
    }
}