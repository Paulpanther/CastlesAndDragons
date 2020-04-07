package server

import model.Pos
import java.lang.Exception

enum class MessageType {
    NAME, MOVE, FINISHED, INVALID
}

val typeStrings = MessageType.values().map { it.name }

open class Message(
        val type: MessageType
) {

    companion object {
        fun parse(message: String?): Message {
            if (message == null) return Message(MessageType.INVALID)
            return try {
                val args = message.split(";")

                val typeStr = findValue(args, "type")
                when (MessageType.values().find { it.name == typeStr }) {
                    MessageType.NAME -> parseName(args)
                    MessageType.MOVE -> parseMove(args)
                    MessageType.FINISHED -> Message(MessageType.FINISHED)
                    else -> Message(MessageType.INVALID)
                }
            } catch (e: Exception) {  // Catch everything, so messages cannot crash the server
                println("Exception while parsing message")
                Message(MessageType.INVALID)
            }
        }

        private fun parseName(args: List<String>): Message {
            val nameStr = findValue(args, "name")
            return if (validName(nameStr)) {
                NameMessage(nameStr!!)
            } else {
                Message(MessageType.INVALID)
            }
        }

        private fun validName(name: String?) = name != null && name.matches("[a-zA-Z0-9_]*".toRegex())

        private fun parseMove(args: List<String>): Message {
            val from = parsePos(findValue(args, "from"))
            val to = parsePos(findValue(args, "to"))

            if (from != null || to != null) {
                return MoveMessage(from, to)
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

        private fun findValue(args: List<String>, key: String): String? {
            val arg = args.find { it.startsWith("$key=") }
            return arg?.substring("$key=".length)
        }
    }
}

class NameMessage(val name: String): Message(MessageType.NAME)
class MoveMessage(val from: Pos?, val to: Pos?): Message(MessageType.MOVE)
