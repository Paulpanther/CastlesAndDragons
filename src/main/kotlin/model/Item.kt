package model

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

    val streets: OrientationMap<StreetType> = Orientations<StreetType>().also {
        it.north = north
        it.west = west
        it.south = south
        it.east = east
    }.build()

    override fun toString(): String {
        return "Item (type = $type)"
    }
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
}

