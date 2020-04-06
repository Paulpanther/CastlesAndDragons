package generator

import model.*
import util.Random

class GridGenerator {

    fun generateRandom(width: Int, height: Int): Grid {
        val grid = Grid(width, height)
        val items = chooseItems()
        choosePositions(items, grid)
        this.chooseRotation(grid)
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
        } while (!this.chooseRotation(grid))
    }

    private fun chooseRotation(grid: Grid): Boolean {
        // Check if every item has at least 1 empty neighbor tile
        for (item in grid) {
            if (!grid.isEmpty(item.x, item.y)) {
                val neighbors = grid.neighborsOf(item.itemState)
                val emptyNeighbor = neighbors
                        .indexOfFirst { n -> n.type == NeighborType.ITEM && n.itemState!!.item == Items.EMPTY}
                if (emptyNeighbor == -1) {
                    return false
                }
                val direction = Orientation.values()[emptyNeighbor]
                grid[item.x, item.y].up = direction.flipYAxis()
            }
        }
        return true
    }

    private fun chooseHeroPos(grid: Grid) {
        val positions = MutableList(grid.width * 2 + grid.height * 2) {
            when {
                it < grid.width -> Pos(it, -1)
                it < grid.width * 2 -> Pos(it - grid.width, grid.height)
                it < grid.width * 2 + grid.height -> Pos(-1, it - grid.width * 2)
                else -> Pos(grid.width, it - (grid.width * 2 + grid.height))
            }
        }
        val validPositions = positions.filter {
            grid.heroPos = it
            grid[grid.heroStartPos()].item == Items.EMPTY
        }
        grid.heroPos = Random.from(validPositions)
    }
}

