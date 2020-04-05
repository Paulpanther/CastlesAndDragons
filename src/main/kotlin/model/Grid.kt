package model

data class Pos(var x: Int, var y: Int)

class Grid(val width: Int, val height: Int) : NeighborSupplier, Iterable<GridIterator.Element> {

    private val itemStates = Array(width * height) { ItemState(Items.EMPTY, this) }
    var heroPos = Pos(0, -1)

    operator fun get(x: Int, y: Int): ItemState {
        check(x, y)
        return itemStates[y * width + x]
    }

    operator fun set(x: Int, y: Int, itemState: ItemState) {
        check(x, y)
        itemStates[y * width + x] = itemState
    }

    fun setItem(x: Int, y: Int, item: Item, up: Orientation = Orientation.NORTH) {
        val state = ItemState(item, this, up)
        this[x, y] = state
    }

    fun setEmpty(x: Int, y: Int): ItemState {
        val old = this[x, y]
        this[x, y] = ItemState(Items.EMPTY, this)
        return old
    }

    fun isEmpty(x: Int, y: Int) = this[x, y].item == Items.EMPTY

    fun clear() {
        itemStates.indices.forEach { itemStates[it] = ItemState(Items.EMPTY, this) }
    }

    override fun neighborsOf(itemState: ItemState): OrientationMap<Neighbor> {
        if (itemState.item.type == ItemType.EMPTY) {
            throw IllegalArgumentException("Cannot find neighbors of empty item")
        }
        val index = itemStates.indexOf(itemState)
        val x = index % width
        val y = index / width
        return Orientations<Neighbor>().apply {
            north = neighborAt(x, y - 1)
            west = neighborAt(x - 1, y)
            south = neighborAt(x, y + 1)
            east = neighborAt(x + 1, y)
        }.build()
    }

    private fun neighborAt(x: Int, y: Int): Neighbor {
        val type = neighborTypeAt(x, y)
        return if (type == NeighborType.ITEM) {
            Neighbor(type, this[x, y])
        } else {
            Neighbor(type)
        }
    }

    private fun neighborTypeAt(x: Int, y: Int): NeighborType {
        return when {
            x == heroPos.x && y == heroPos.y -> {
                NeighborType.HERO
            }
            x < 0 || x >= width || y < 0 || y >= height -> {
                NeighborType.BORDER
            }
            else -> {
                NeighborType.ITEM
            }
        }
    }

    private fun check(x: Int, y: Int) {
        if (x >= width || x < 0) {
            throw IndexOutOfBoundsException("value of x ($x) not in width ($width)")
        }
        if (y >= height || y < 0) {
            throw IndexOutOfBoundsException("value of y ($y) not in height ($height)")
        }
    }

    private fun toRectArray(): List<List<ItemState>> {
        return (0 until height).map { y -> (0 until width).map { x -> this[x, y] } }
    }

    override fun iterator(): Iterator<GridIterator.Element> {
        return GridIterator(this)
    }

    override fun toString(): String {
        val rows = toRectArray()
        val rowsStrings = rows.map { it.joinToString("\t|\t", "\t") }
        return rowsStrings.joinToString("\n", "Grid:\n", "\n")
    }

    fun toShortString(): String {
        return itemStates.joinToString("", "${width}w${height}h") { it.toShortString() }
    }
}

class GridIterator(private val grid: Grid) : Iterator<GridIterator.Element> {

    private var index = 0

    override fun hasNext() = index < grid.width * grid.height

    override fun next(): Element {
        val x = index % grid.width
        val y = index / grid.width
        index++
        return Element(x, y, grid[x, y])
    }

    data class Element(
            val x: Int,
            val y: Int,
            val itemState: ItemState
    )
}
