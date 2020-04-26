package com.paulmethfessel.cad.server

import com.paulmethfessel.cad.model.*
import com.paulmethfessel.cad.util.ListExt.reverse
import org.slf4j.MarkerFactory
import java.lang.Exception

private val tag = MarkerFactory.getMarker("MESSAGE_PARSER")

enum class MessageType {
    NAME, MOVE, FINISHED, INVALID, RESTART
}

open class Message(
        val type: MessageType
) {

    companion object {
        fun parse(message: String?): Message {
            if (message == null) return Message(MessageType.INVALID)
            return try {
                val args = message.split(";")

                val typeStr = findValue(args, "type")
                when (MessageType.values().find { it.name.toLowerCase() == typeStr?.toLowerCase() }) {
                    MessageType.NAME -> parseName(args)
                    MessageType.MOVE -> parseMove(args)
                    MessageType.FINISHED -> Message(MessageType.FINISHED)
                    MessageType.RESTART -> Message(MessageType.RESTART)
                    else -> Message(MessageType.INVALID)
                }
            } catch (e: Exception) {  // Catch everything, so messages cannot crash the server
                Server.log.error(tag, "Exception while parsing message", e)
                Message(MessageType.INVALID)
            }
        }

        private fun parseName(args: List<String>): Message {
            val nameStr = findValue(args, "name")
            return if (nameStr != null) {
                NameMessage(nameStr)
            } else {
                Message(MessageType.INVALID)
            }
        }

        private fun parseMove(args: List<String>): Message {
            val from = parsePos(findValue(args, "from"))
            val to = parsePos(findValue(args, "to"))
            val up = parseOrientation(findValue(args, "up"))
            val item = parseItem(findValue(args, "item"))

            if ((from != null || to != null && up != null) && item != null) {
                return MoveMessage(from, to, up, item)
            }

            return Message(MessageType.INVALID)
        }

        private fun parsePos(str: String?): Pos? {
            if (str != null) {
                val positionsStr = str.split(",")
                if (positionsStr.all { it.matches("\\d+".toRegex()) } && positionsStr.size == 2) {
                    val x = positionsStr[0].toInt()
                    val y = positionsStr[1].toInt()
                    return Pos(x, y)
                }
            }
            return null
        }

        private fun parseOrientation(str: String?): Orientation? {
            return shortOrientations.reverse()[str]
        }

        private fun parseItem(str: String?): Item? {
            return Items.shortNames.reverse()[str]
        }

        private fun findValue(args: List<String>, key: String): String? {
            val arg = args.find { it.startsWith("$key=") }
            return arg?.substring("$key=".length)
        }
    }
}

class NameMessage(val name: String): Message(MessageType.NAME)
class MoveMessage(val from: Pos?, val to: Pos?, val up: Orientation?, val item: Item): Message(MessageType.MOVE)
