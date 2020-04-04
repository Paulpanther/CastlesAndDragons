package main.kotlin.model


class ItemState(
        val item: Item,
        private val neighborSupplier: NeighborSupplier,
        val up: Orientation = Orientation.NORTH) {

    fun isConnectionWithNeighborValid(directionWorld: Orientation): Boolean {
        val directionLocal = OrientationTransformer.worldToLocal(directionWorld, up)
        val neighbor = neighborSupplier.neighborsOf(this)[directionWorld]
        val street = item.streets[directionLocal]

        return if (neighbor.type == NeighborType.ITEM) {
            val neighborStreet = neighbor.itemState!!.streetAt(OrientationTransformer.opposite(directionWorld))
            isStreetToStreetValid(street, neighborStreet, neighbor.itemState.item.type)
        } else {
            isStreetToEmptyValid(street)
        }
    }

    fun streetAt(directionWorld: Orientation): StreetType {
        val directionLocal = OrientationTransformer.worldToLocal(directionWorld, up)
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

    private fun isStreetToEmptyValid(street: StreetType): Boolean {
        return item.type == ItemType.DRAGON || street == StreetType.NONE
    }
}
