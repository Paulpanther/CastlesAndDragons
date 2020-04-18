package com.paulmethfessel.cad.model


class ItemState(
        val item: Item,
        private val neighborSupplier: NeighborSupplier,
        var up: Orientation = Orientation.NORTH) {

    fun isConnectionWithNeighborValid(directionWorld: Orientation): Boolean {
        val neighbor = neighborSupplier.neighborsOf(this)[directionWorld]

        return if (neighbor.type == NeighborType.ITEM) {
            isConnectionWithItemValid(neighbor, directionWorld)
        } else {
            true
        }
    }

    fun getConnectedNeighbors(): List<ItemState> {
        val neighbors = neighborSupplier.neighborsOf(this)
        return Orientation.values().filter {
            val neighbor = neighbors[it]
            neighbor.type == NeighborType.ITEM &&
                    streetAt(it) != StreetType.NONE &&
                    neighbor.itemState!!.streetAt(it.opposite()) == streetAt(it)
        }.map { neighbors[it].itemState!! }
    }

    fun streetAt(directionWorld: Orientation): StreetType {
        val directionLocal = directionWorld.toLocal(up)
        return item.streets[directionLocal]
    }

    private fun isConnectionWithItemValid(neighbor: Neighbor, directionWorld: Orientation): Boolean {
        val directionLocal = directionWorld.toLocal(up)
        val street = item.streets[directionLocal]

        val neighborStreet = neighbor.itemState!!.streetAt(directionWorld.opposite())
        return isStreetToStreetValid(street, neighborStreet, neighbor.itemState.item.type)
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
