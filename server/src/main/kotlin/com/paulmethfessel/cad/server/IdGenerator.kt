package com.paulmethfessel.cad.server

import com.paulmethfessel.cad.util.Random

private const val idLength = 8
private val source = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

object IdGenerator {

    fun generateId(idsInUse: List<Int>): Int {
        var id = 0
        while (id in idsInUse) { id++ }
        return id
    }

    fun generateRandomId(idsInUse: List<String>): String {
        var id: String
        do {
            id = Random.getString(idLength, source)
        } while (id in idsInUse)
        return id
    }
}