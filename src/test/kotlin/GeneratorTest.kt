import generator.RecursiveGenerator
import generator.connect
import model.Grid
import model.Items
import model.Orientation
import model.Pos
import kotlin.test.*

class GeneratorTest {

    private lateinit var generator: RecursiveGenerator
    private lateinit var grid: Grid

    @BeforeTest
    fun setup() {
        grid = Grid(5, 3)
        grid.setItem(3, 0, Items.FOUR)
        grid.setItem(3, 1, Items.LINE_BOTH, Orientation.SOUTH)
        grid.setItem(0, 0, Items.DRAGON_MUD)

        generator = RecursiveGenerator(5, 3, grid)
    }

    @Test
    fun testOpenConnectionsFrom() {
        val connections = generator.getOpenConnectionsFrom(Pos(3, 0))
        assertEquals(listOf(
                Pos(3, 0) connect Pos(2, 0),
                Pos(3, 0) connect Pos(4, 0)
        ), connections)
        assertEquals(listOf(), generator.getOpenConnectionsTo(Pos(1, 0)))
    }

    @Test
    fun testOpenConnectionsTo() {
        assertEquals(listOf(Pos(3, 0) connect Pos(4, 0)), generator.getOpenConnectionsTo(Pos(4, 0)))
        assertEquals(listOf(), generator.getOpenConnectionsTo(Pos(4, 1)))
        assertEquals(listOf(), generator.getOpenConnectionsTo(Pos(0, 0)))
    }

    @Test
    fun testIsItemValid() {
        val oldStr = grid.toShortString()

        assertTrue(generator.isItemValid(Pos(3, 2), Items.THREE_1, Orientation.EAST))
        assertFalse(generator.isItemValid(Pos(3, 2), Items.THREE_1, Orientation.NORTH))
        assertFalse(generator.isItemValid(Pos(3, 2), Items.THREE_1, Orientation.WEST))
        assertTrue(generator.isItemValid(Pos(2, 0), Items.CASTLE_STONE_1, Orientation.EAST))

        assertEquals(oldStr, grid.toShortString())
    }
}