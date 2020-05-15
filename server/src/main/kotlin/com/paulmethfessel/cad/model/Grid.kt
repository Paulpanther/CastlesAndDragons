package com.paulmethfessel.cad.model

class Grid(val width: Int, val height: Int) : NeighborSupplier, Iterable<GridIterator.Element> {

    private val itemStates = Array(width * height) { ItemState(Items.EMPTY, this) }
    var heroPos = Pos(0, -1)

    fun canBeSet(item: Item) = item == Items.EMPTY || all { it.itemState.item != item }

    operator fun get(x: Int, y: Int): ItemState {
        check(x, y)
        return itemStates[y * width + x]
    }

    operator fun get(pos: Pos) = get(pos.x, pos.y)

    operator fun set(x: Int, y: Int, itemState: ItemState) {
        check(x, y)
        if (!canBeSet(itemState.item)) {
            throw IllegalArgumentException("Item is already present in grid")
        }

        itemStates[y * width + x] = itemState
    }

    operator fun set(pos: Pos, itemState: ItemState) {
        set(pos.x, pos.y, itemState)
    }

    fun setItem(pos: Pos, item: Item, up: Orientation = Orientation.NORTH) {
        setItem(pos.x, pos.y, item, up)
    }

    fun setItem(x: Int, y: Int, item: Item, up: Orientation = Orientation.NORTH) {
        val state = ItemState(item, this, up)
        this[x, y] = state
    }

    fun setEmpty(pos: Pos) {
        setEmpty(pos.x, pos.y)
    }

    fun setEmpty(x: Int, y: Int): ItemState {
        val old = this[x, y]
        this[x, y] = ItemState(Items.EMPTY, this)
        return old
    }

    fun isEmpty(pos: Pos) = isEmpty(pos.x, pos.y)

    fun isEmpty(x: Int, y: Int) = this[x, y].item == Items.EMPTY

    fun clear() {
        itemStates.indices.forEach { itemStates[it] = ItemState(Items.EMPTY, this) }
    }

    fun heroStartPos(): Pos {
        return when {
            heroPos.x == -1 -> heroPos + Pos(1, 0)
            heroPos.x == width -> heroPos - Pos(1, 0)
            heroPos.y == -1 -> heroPos + Pos(0, 1)
            else -> heroPos - Pos(0, 1)
        }
    }

    fun posOf(itemState: ItemState): Pos {
        if (itemState.item.type == ItemType.EMPTY) {
            throw IllegalArgumentException("Cannot find position of empty item")
        }
        return Pos.fromIndex(itemStates.indexOf(itemState), width)
    }

    override fun neighborsOf(itemState: ItemState): OrientationMap<Neighbor> {
        return neighborsOf(posOf(itemState))
    }

    fun neighborsOf(pos: Pos): OrientationMap<Neighbor> {
        return MutableOrientationMap<Neighbor>().apply {
            north = neighborAt(pos.x, pos.y - 1)
            west = neighborAt(pos.x - 1, pos.y)
            south = neighborAt(pos.x, pos.y + 1)
            east = neighborAt(pos.x + 1, pos.y)
        }.toOrientationMap()
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

    fun inGrid(pos: Pos) = inGrid(pos.x, pos.y)

    fun inGrid(x: Int, y: Int) = !(xNotInGrid(x) || yNotInGrid(y))

    private fun check(x: Int, y: Int) {
        if (xNotInGrid(x)) {
            throw IndexOutOfBoundsException("value of x ($x) not in width ($width)")
        }
        if (yNotInGrid(y)) {
            throw IndexOutOfBoundsException("value of y ($y) not in height ($height)")
        }
    }

    private fun xNotInGrid(x: Int) = x >= width || x < 0
    private fun yNotInGrid(y: Int) = y >= height || y < 0

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

    fun clone(): Grid {
        val cloned = Grid(width, height)
        val states = itemStates.map { ItemState(it.item, cloned, it.up) }
        cloned.itemStates.forEachIndexed { i, _ -> cloned.itemStates[i] = states[i] }
        cloned.heroPos = heroPos.clone()
        return cloned
    }

    fun toShortString(): String {
        val hero = heroToShortString()
        return itemStates.joinToString("", "${width}w${height}h${hero}p") { it.toShortString() }
    }

    fun toLongString(): String {
        return filter { it.itemState.item != Items.EMPTY }
                .joinToString("", "Grid (hero=${heroToShortString()})") {
                    "\n\tItem=${it.itemState.item.type},\tUp=${it.itemState.up},\tPos=${it.pos}"
                }
    }

    private fun heroToShortString(): String {
        var side = "?"
        var value = 0
        when {
            heroPos.y == -1 -> {
                side = "0"
                value = heroPos.x
            }
            heroPos.x == -1 -> {
                side = "1"
                value = heroPos.y
            }
            heroPos.y == height -> {
                side = "2"
                value = heroPos.x
            }
            heroPos.x == width -> {
                side = "3"
                value = heroPos.y
            }
        }
        return "$side$value"
    }
}

class GridIterator(private val grid: Grid) : Iterator<GridIterator.Element> {

    private var index = 0

    override fun hasNext() = index < grid.width * grid.height

    override fun next(): Element {
        val pos = Pos.fromIndex(index++, grid.width)
        return Element(pos.x, pos.y, grid[pos])
    }

    data class Element(
            val x: Int,
            val y: Int,
            val itemState: ItemState) {
        val pos = Pos(x, y)
    }
}
