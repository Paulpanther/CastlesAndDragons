package model


class ItemState(
        val item: Item,
        private val neighborSupplier: NeighborSupplier,
        var up: Orientation = Orientation.NORTH) {

    fun isConnectionWithNeighborValid(directionWorld: Orientation): Boolean {
        val directionLocal = directionWorld.toLocal(up)
        val neighbor = neighborSupplier.neighborsOf(this)[directionWorld]
        val street = item.streets[directionLocal]

        return if (neighbor.type == NeighborType.ITEM) {
            val neighborStreet = neighbor.itemState!!.streetAt(directionWorld.opposite())
            isStreetToStreetValid(street, neighborStreet, neighbor.itemState.item.type)
        } else {
            true
        }
    }

    private fun streetAt(directionWorld: Orientation): StreetType {
        val directionLocal = directionWorld.toLocal(up)
        return item.streets[directionLocal]
    }

    private fun isStreetToStreetValid(ourStreet: StreetType, neighborStreet: StreetType, neighborType: ItemType): Boolean {
        return when {
            item.type == ItemType.DRAGON && neighborType == ItemType.DRAGON -> {
                true
            }
            item.type == ItemType.DRAGON -> {
                neighborStreet == StreetType.NONE || neighborStreet == ourStreet
            }
            neighborType == ItemType.DRAGON -> {
                ourStreet == StreetType.NONE || neighborStreet == ourStreet
            }
            else -> {
                neighborStreet == ourStreet
            }
        }
    }

    override fun toString() = "($item, $up)"
    fun toShortString() = item.toShortString() + if (item != Items.EMPTY) up.toShortString() else ""
}
