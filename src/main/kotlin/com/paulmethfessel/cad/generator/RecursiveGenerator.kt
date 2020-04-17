package com.paulmethfessel.cad.generator

import com.paulmethfessel.cad.model.*
import com.paulmethfessel.cad.util.Random

class RecursiveGenerator(
        private val width: Int,
        private val height: Int,
        private val grid: Grid = Grid(width, height)
) {

    private val openConnections = mutableListOf<Connection>()
    private var placedDragonRed = false
    private var placedDragonBlue = false
    private var placedCastlesRedCount = 0
    private var placedCastlesBlueCount = 0
    private val usedItems = mutableListOf<Item>()

    fun generate(): Grid {
        val hero = chooseHeroPos()
        grid.heroPos = hero
        openConnections.add(Connection(hero, grid.heroStartPos()))

        // recursively choose items until grid is finished
        chooseNextItem()

        removeStreetsFromGrid()
        return grid
    }

    private fun chooseNextItem(): Boolean {
        // Shuffle so different grids can be generated
        val possibilities = Random.shuffle(getAllConnectionItemPossibilities(openConnections))
        if (possibilities.isEmpty()) return false

        for (possibility in possibilities) {
            val pos = possibility.connection.to
            placeItem(pos, possibility.itemPossibility.item, possibility.itemPossibility.up)

            if (areCastlesAndDragonsInGrid() && openConnections.isEmpty()) {
                // Grid is finished
                return true
            } else if (openConnections.isEmpty()){
                // No open connections, but not all necessary items are placed
                removeItem(pos)
                continue
            } else {
                if (chooseNextItem()) {
                    // Grid is finished
                    return true
                } else {
                    // Possibility is not working
                    removeItem(pos)
                }
            }
        }

        // No possibility was working, so return not finished (prev item will be removed)
        return false
    }

    private fun getAllConnectionItemPossibilities(connections: List<Connection>): List<ConnectionPossibility> {
        return connections.flatMap { getAllValidPossibilitiesForConnection(it) }
    }

    private fun getAllValidPossibilitiesForConnection(connection: Connection): List<ConnectionPossibility> {
        val items = Items.all.filter { it != Items.EMPTY }

        val possibilities = mutableListOf<ConnectionPossibility>()
        for (item in items) {
            for (orientation in Orientation.values()) {
                if (isItemValid(connection.to, item, orientation) && isItemConnected(item, orientation, connection)) {
                    possibilities.add(ConnectionPossibility(connection, ItemPossibility(item, orientation)))
                }
            }
        }
        return possibilities
    }

    fun isItemValid(pos: Pos, item: Item, up: Orientation): Boolean {
        if (item in usedItems) {
            return false
        }

        val oldState = grid[pos]
        grid.setItem(pos, item, up)
        val state = grid[pos]
        val valid = Orientation.values().all {
            val neighbor = grid.neighborsOf(state)[it]
            state.isConnectionWithNeighborValid(it) ||
                    neighbor.type == NeighborType.ITEM && neighbor.itemState!!.item == Items.EMPTY
        }
        grid[pos] = oldState
        return valid
    }

    private fun isItemConnected(item: Item, up: Orientation, connection: Connection): Boolean {
        val streetDir = connection.direction.opposite().toLocal(up)
        return item.streets[streetDir] != StreetType.NONE
    }

    private fun placeItem(pos: Pos, item: Item, up: Orientation) {
        grid.setItem(pos, item, up)
        if (item == Items.DRAGON_STONE) placedDragonBlue = true
        if (item == Items.DRAGON_MUD) placedDragonRed = true
        if (item == Items.CASTLE_STONE_2 || item == Items.CASTLE_STONE_1) placedCastlesBlueCount++
        if (item == Items.CASTLE_MUD_1 || item == Items.CASTLE_MUD_2) placedCastlesRedCount++

        openConnections.removeIf { it.to == pos }
        openConnections.addAll(getOpenConnectionsFrom(pos))
        usedItems.add(item)
    }

    private fun removeItem(pos: Pos) {
        val state = grid[pos]
        val item = state.item
        if (item == Items.DRAGON_STONE) placedDragonBlue = false
        if (item == Items.DRAGON_MUD) placedDragonRed = false
        if (item == Items.CASTLE_STONE_2 || item == Items.CASTLE_STONE_1) placedCastlesBlueCount--
        if (item == Items.CASTLE_MUD_1 || item == Items.CASTLE_MUD_2) placedCastlesRedCount--

        openConnections.removeAll(getOpenConnectionsFrom(pos))
        openConnections.addAll(getOpenConnectionsTo(pos))

        grid.setEmpty(pos.x, pos.y)
        usedItems.remove(item)
    }

    fun getOpenConnectionsTo(pos: Pos): List<Connection> {
        return Orientation.values().filter {
            val from = pos.moveInOrientation(it)
            isOpenConnectionFromTo(from, pos) && grid.inGrid(from)
        }.map { Connection(pos.moveInOrientation(it), pos) }
    }

    fun getOpenConnectionsFrom(pos: Pos): List<Connection> {
        return Orientation.values().filter {
            val to = pos.moveInOrientation(it)
            isOpenConnectionFromTo(pos, to) && grid.inGrid(to)
        }.map { Connection(pos, pos.moveInOrientation(it)) }
    }

    private fun isOpenConnectionFromTo(from: Pos, to: Pos): Boolean {
        if (grid.inGrid(from)) {
            val fromState = grid[from]
            val dir = from.whichMoveOrientation(to)
            if (fromState.item == Items.EMPTY ||
                    fromState.item.type == ItemType.DRAGON ||
                    fromState.streetAt(dir) == StreetType.NONE) {
                return false
            }
        }
        if (grid.inGrid(to)) {
            val toState = grid[to]
            if (toState.item != Items.EMPTY) {
                return false
            }
        }
        return true
    }

    private fun removeStreetsFromGrid() {
        for (element in grid) {
            val type = element.itemState.item.type
            if (type != ItemType.DRAGON && type != ItemType.CASTLE) {
                grid.setEmpty(element.pos)
            }
        }
    }

    private fun areCastlesAndDragonsInGrid(): Boolean {
        return placedDragonBlue && placedDragonRed && placedCastlesBlueCount >= 1 && placedCastlesRedCount >= 1
    }

    private fun chooseHeroPos(): Pos {
        val positions = MutableList(width * 2 + height * 2) {
            when {
                it < width -> Pos(it, -1)
                it < width * 2 -> Pos(it - width, height)
                it < width * 2 + height -> Pos(-1, it - width * 2)
                else -> Pos(width, it - (width * 2 + height))
            }
        }
        return Random.from(positions)
    }
}

data class ConnectionPossibility(
        val connection: Connection,
        val itemPossibility: ItemPossibility)

data class ItemPossibility(
        val item: Item,
        val up: Orientation)

data class Connection(
        val from: Pos,
        val to: Pos) {
    val direction = from.whichMoveOrientation(to)
}

infix fun Pos.connect(other: Pos) = Connection(this, other)