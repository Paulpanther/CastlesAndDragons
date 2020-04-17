package com.paulmethfessel.cad.model

enum class ItemType {
    CASTLE, DRAGON, STREET, EMPTY
}

enum class StreetType {
    STONE, MUD, NONE
}

class Item(
        val type: ItemType,
        north: StreetType = StreetType.NONE,
        west: StreetType = StreetType.NONE,
        south: StreetType = StreetType.NONE,
        east: StreetType = StreetType.NONE) {

    val streets: OrientationMap<StreetType> = MutableOrientationMap<StreetType>().also {
        it.north = north
        it.west = west
        it.south = south
        it.east = east
    }.toOrientationMap()

    override fun toString(): String {
        return "Item (type = $type)"
    }

    fun toShortString() = Items.shortNames[this]?: "?"
}

object Items {
    val EMPTY = Item(ItemType.EMPTY)
    val LINE_MUD = Item(ItemType.STREET, north = StreetType.MUD, south = StreetType.MUD)
    val LINE_STONE = Item(ItemType.STREET, north = StreetType.STONE, south = StreetType.STONE)
    val LINE_BOTH = Item(ItemType.STREET, north = StreetType.STONE, south = StreetType.MUD)
    val THREE_1 = Item(ItemType.STREET, north = StreetType.MUD, east = StreetType.STONE, south = StreetType.STONE)
    val THREE_2 = Item(ItemType.STREET, north = StreetType.STONE, east = StreetType.STONE, south = StreetType.MUD)
    val THREE_3 = Item(ItemType.STREET, north = StreetType.STONE, east = StreetType.MUD, south = StreetType.STONE)
    val CORNER_1 = Item(ItemType.STREET, north = StreetType.STONE, east = StreetType.MUD)
    val CORNER_2 = Item(ItemType.STREET, north = StreetType.MUD, east = StreetType.STONE)
    val CASTLE_STONE_1 = Item(ItemType.CASTLE, south = StreetType.STONE)
    val CASTLE_STONE_2 = Item(ItemType.CASTLE, south = StreetType.STONE)
    val CASTLE_MUD_1 = Item(ItemType.CASTLE, south = StreetType.MUD)
    val CASTLE_MUD_2 = Item(ItemType.CASTLE, south = StreetType.MUD)
    val DRAGON_STONE = Item(ItemType.DRAGON, StreetType.STONE, StreetType.STONE, StreetType.STONE, StreetType.STONE)
    val DRAGON_MUD = Item(ItemType.DRAGON, north = StreetType.MUD, south = StreetType.MUD)
    val FOUR = Item(ItemType.STREET, north = StreetType.MUD, west = StreetType.STONE, south = StreetType.MUD, east = StreetType.MUD)

    val shortNames = mapOf(
            EMPTY to "0",
            LINE_MUD to "1",
            LINE_STONE to "2",
            LINE_BOTH to "3",
            THREE_1 to "4",
            THREE_2 to "5",
            THREE_3 to "6",
            CORNER_1 to "7",
            CORNER_2 to "8",
            CASTLE_STONE_1 to "9",
            CASTLE_STONE_2 to "a",
            CASTLE_MUD_1 to "b",
            CASTLE_MUD_2 to "c",
            DRAGON_STONE to "d",
            DRAGON_MUD to "e",
            FOUR to "f"
    )

    val all = listOf(EMPTY, LINE_MUD, LINE_STONE, LINE_BOTH, THREE_1, THREE_2, THREE_3, CORNER_1, CORNER_2,
            CASTLE_STONE_1, CASTLE_STONE_2, CASTLE_MUD_1, CASTLE_MUD_2, DRAGON_STONE, DRAGON_MUD, FOUR)
}
