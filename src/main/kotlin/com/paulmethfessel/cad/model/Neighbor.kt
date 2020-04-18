package com.paulmethfessel.cad.model

interface NeighborSupplier {
    fun neighborsOf(itemState: ItemState): OrientationMap<Neighbor>
}

data class Neighbor(
        val type: NeighborType,
        val itemState: ItemState? = null)

enum class NeighborType {
    BORDER, ITEM, HERO
}
