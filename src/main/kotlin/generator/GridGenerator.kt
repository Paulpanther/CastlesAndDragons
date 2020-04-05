package generator

import model.*
import util.Random

class GridGenerator {

    fun generateRandom(width: Int, height: Int): Grid {
        val grid = Grid(width, height)
        val items = chooseItems()
        choosePositions(items, grid)
        chooseRotation(grid)
        chooseHeroPos(grid)
        return grid
    }

    private fun chooseItems(): List<Item> {
        val items = mutableListOf(Items.DRAGON_MUD, Items.DRAGON_STONE, Items.CASTLE_MUD_1, Items.CASTLE_STONE_1)
        if (Random.bool()) items.add(Items.CASTLE_STONE_2)
        if (Random.bool()) items.add(Items.CASTLE_MUD_2)
        return items
    }

    private fun choosePositions(items: List<Item>, grid: Grid) {
        do {
            grid.clear()
            val positionIndices = Random.multipleFrom(items.size, 0 until (grid.width * grid.height))
            positionIndices.forEachIndexed { index, it ->
                val x = it % grid.width
                val y = it / grid.width
                grid.setItem(x, y, items[index])
            }
        } while (!gridPositionsValid(grid))
    }

    private fun gridPositionsValid(grid: Grid): Boolean {
        grid.forEach {
            if (!grid.isEmpty(it.x, it.y)) {
                val neighbors = grid.neighborsOf(it.itemState)
                if (!neighbors.any { n -> n.type == NeighborType.ITEM && n.itemState!!.item == Items.EMPTY }) {
                    return false
                }
            }
        }
        return true
    }

    private fun chooseRotation(grid: Grid) {
        // TODO
    }

    private fun chooseHeroPos(grid: Grid) {
        // TODO
    }
}

