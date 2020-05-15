package com.paulmethfessel.cad

import com.paulmethfessel.cad.generator.RecursiveGenerator
import com.paulmethfessel.cad.server.Config
import com.paulmethfessel.cad.server.Server
import com.xenomachina.argparser.ArgParser

fun main(args: Array<String>) {
    if ("--generate" in args) {
        println(RecursiveGenerator(5, 3).generate().toLongString())
    } else {
        Server.start(ArgParser(args).parseInto(::Config))
    }
}
