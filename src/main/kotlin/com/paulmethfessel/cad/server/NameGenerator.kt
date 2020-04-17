package com.paulmethfessel.cad.server

object NameGenerator {

    fun generateName(namesInUse: List<String>): String {
        var nr = 0
        var name: String
        do {
            name = "Hans${nr++}"
        } while (namesInUse.contains(name))
        return name
    }

    fun validName(taken: List<String>, name: String) = name !in taken && name.matches("[a-zA-Z0-9_]+".toRegex())
}