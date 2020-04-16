import model.*
import java.lang.IllegalArgumentException
import kotlin.test.*


class GridTest {

    private lateinit var grid: Grid
    private lateinit var state: ItemState

    @BeforeTest
    fun setup() {
        grid = Grid(15, 5)
        state = ItemState(Items.CASTLE_MUD_1, grid, Orientation.SOUTH)
    }

    @Test
    fun testCreation() {
        assertTrue(grid.all { it.itemState.item == Items.EMPTY })
        assertEquals(15, grid.width)
        assertEquals(5, grid.height)
    }

    @Test
    fun testSetGet() {
        grid[Pos(3, 4)] = state
        grid.setItem(Pos(10, 2), Items.CORNER_1)

        // Test getters
        assertEquals(Items.CASTLE_MUD_1, grid[3, 4].item)
        assertEquals(Orientation.SOUTH, grid[3, 4].up)
        assertEquals(Items.CORNER_1, grid[10, 2].item)
        assertEquals(Orientation.NORTH, grid[10, 2].up)
        assertFalse(grid.isEmpty(10, 2))

        // Test set Empty
        grid.setEmpty(Pos(10, 2))
        assertEquals(Items.EMPTY, grid[10, 2].item)
        assertTrue(grid.isEmpty(Pos(10, 2)))

        // Test clear
        grid.setItem(2, 3, Items.CASTLE_MUD_2)
        grid.setItem(2, 4, Items.DRAGON_MUD)
        grid.clear()
        assertTrue(grid.isEmpty(2, 3))
        assertTrue(grid.isEmpty(2, 4))

        // Test no duplicates in grid
        grid.setItem(4, 3, Items.DRAGON_MUD)
        assertFails { grid.setItem(2, 3, Items.DRAGON_MUD) }
    }

    @Test
    fun testIterator() {
        assertEquals(15 * 5, grid.map { it }.size)
    }

    @Test
    fun testNeighbors() {
        grid.heroPos = Pos(9, 5)
        grid[9, 4] = state
        grid.setItem(10, 4, Items.DRAGON_MUD)
        val neighbors = grid.neighborsOf(state)
        assertEquals(NeighborType.HERO, neighbors[Orientation.SOUTH].type)
        assertNotNull(neighbors[Orientation.WEST].itemState)
        assertEquals(Items.EMPTY, neighbors[Orientation.WEST].itemState!!.item)
        assertNotNull(neighbors[Orientation.EAST].itemState)
        assertEquals(Items.DRAGON_MUD, neighbors[Orientation.EAST].itemState!!.item)
    }

    @Test
    fun testHeroPos() {
        grid.heroPos = Pos(2, 5)
        assertEquals(Pos(2, 4), grid.heroStartPos())
        grid.heroPos = Pos(5, -1)
        assertEquals(Pos(5, 0), grid.heroStartPos())
        grid.heroPos = Pos(-1, 2)
        assertEquals(Pos(0, 2), grid.heroStartPos())
        grid.heroPos = Pos(15, 2)
        assertEquals(Pos(14, 2), grid.heroStartPos())
    }

    @Test
    fun testPosOf() {
        grid[3, 2] = state
        assertEquals(Pos(3, 2), grid.posOf(state))
        assertFails { grid.posOf(grid[0, 0]) }
    }
}