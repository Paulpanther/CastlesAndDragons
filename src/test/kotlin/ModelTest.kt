import model.*
import kotlin.test.*

class ModelTest {

    private lateinit var grid: Grid
    private lateinit var state: ItemState

    @BeforeTest
    fun setup() {
        grid = Grid(15, 5)
        state = ItemState(Items.CASTLE_MUD_1, grid, Orientation.SOUTH)
    }

    @Test
    fun testOrientationTransformer() {
        assertEquals(Orientation.NORTH, Orientation.SOUTH.opposite())
        assertEquals(Orientation.EAST, Orientation.WEST.opposite())
        assertEquals(Orientation.WEST, Orientation.EAST.opposite())

        assertEquals(Orientation.NORTH, Orientation.NORTH.toLocal(Orientation.NORTH))
        assertEquals(Orientation.EAST, Orientation.SOUTH.toLocal(Orientation.WEST))
        assertEquals(Orientation.SOUTH, Orientation.WEST.toLocal(Orientation.WEST))

        assertEquals(Orientation.WEST, Orientation.EAST.toWorld(Orientation.SOUTH))
        assertEquals(Orientation.SOUTH, Orientation.SOUTH.toWorld(Orientation.NORTH))

        assertEquals(Orientation.WEST, Orientation.EAST.flipXAxis())
        assertEquals(Orientation.NORTH, Orientation.NORTH.flipXAxis())
        assertEquals(Orientation.WEST, Orientation.WEST.flipYAxis())
        assertEquals(Orientation.NORTH, Orientation.SOUTH.flipYAxis())
    }

    @Test
    fun testOrientationBuilder() {
        val orientations = MutableOrientationMap<Int>().apply {
            north = 0
            west = 1
            south = 2
            east = 3
        }.toOrientationMap()
        assertEquals(0, orientations[Orientation.NORTH])
        assertEquals(1, orientations[Orientation.WEST])
        assertEquals(2, orientations[Orientation.SOUTH])
        assertEquals(3, orientations[Orientation.EAST])
    }

    @Test
    fun testIsConnectionWithNeighborValid1() {
        grid.apply {
            setItem(6, 0, Items.THREE_1)
            setItem(5, 0, Items.DRAGON_MUD)
            setItem(7, 0, Items.LINE_BOTH, Orientation.EAST)
            setItem(6, 1, Items.CASTLE_STONE_1, Orientation.SOUTH)
        }
        val state = grid[6, 0]
        assertTrue(state.isConnectionWithNeighborValid(Orientation.NORTH), "Connection to border is always valid")
        assertTrue(state.isConnectionWithNeighborValid(Orientation.WEST), "Connection to dragon valid, because no street to dragon")
        assertTrue(state.isConnectionWithNeighborValid(Orientation.SOUTH), "Connection to castle valid, because both have stone street")
        assertTrue(state.isConnectionWithNeighborValid(Orientation.EAST), "Connection to line valid, because both have stone street")
    }

    @Test
    fun testIsConnectionWithNeighborValid2() {
        grid.apply {
            setItem(9,3, Items.CORNER_1, Orientation.EAST)  // Center
            setItem(9, 2, Items.EMPTY)  // North
            setItem(8, 3, Items.THREE_2, Orientation.EAST)  // West
            setItem(9, 4, Items.THREE_1)  // South
            setItem(10, 3, Items.CASTLE_STONE_1, Orientation.WEST)  // East
        }
        val state = grid[9, 3]
        assertFalse(state.isConnectionWithNeighborValid(Orientation.NORTH), "Connection to empty not valid, because street meets empty")
        assertFalse(state.isConnectionWithNeighborValid(Orientation.WEST), "Connection to three not valid, because streets don't match")
        assertFalse(state.isConnectionWithNeighborValid(Orientation.SOUTH), "Connection to three not valid, because street meets no street")
        assertFalse(state.isConnectionWithNeighborValid(Orientation.EAST), "Connection to castle not valid, because street from castle meets no street")
    }

    @Test
    fun testOrientationTo() {
        assertEquals(Orientation.NORTH, Pos(0, 0).whichMoveOrientation(Pos(0, -1)))
        assertEquals(Orientation.WEST, Pos(4, 5).whichMoveOrientation(Pos(3, 5)))
        assertEquals(Orientation.SOUTH, Pos(9, 3).whichMoveOrientation(Pos(9, 4)))
        assertEquals(Orientation.EAST, Pos(2, 4).whichMoveOrientation(Pos(3, 4)))
    }

    @Test
    fun moveInOrientation() {
        assertEquals(Pos(0, -1), Pos(0, 0).moveInOrientation(Orientation.NORTH))
        assertEquals(Pos(3, 5), Pos(4, 5).moveInOrientation(Orientation.WEST))
        assertEquals(Pos(9, 4), Pos(9, 3).moveInOrientation(Orientation.SOUTH))
        assertEquals(Pos(3, 4), Pos(2, 4).moveInOrientation(Orientation.EAST))
    }
}