package com.paulmethfessel.cad

import com.paulmethfessel.cad.server.Config
import com.paulmethfessel.cad.server.Server
import com.xenomachina.argparser.ArgParser

fun main(args: Array<String>) {
    Server.start(ArgParser(args).parseInto(::Config))
}
