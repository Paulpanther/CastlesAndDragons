import com.paulmethfessel.cad.model.Grid
import com.paulmethfessel.cad.model.Items
import com.paulmethfessel.cad.model.Orientation
import com.paulmethfessel.cad.model.Pos
import com.paulmethfessel.cad.solver.GridSolver
import kotlin.test.*

class SolverTest {

    private lateinit var finishedGrid1: Grid
    private lateinit var finishedGrid2: Grid
    private lateinit var unfinishedGrid1: Grid
    private lateinit var unfinishedGrid2: Grid
    private lateinit var unfinishedGrid3: Grid

    @BeforeTest
    fun setup() {
        finishedGrid1 = Grid(15, 5)
        finishedGrid1.heroPos = Pos(3, -1)
        finishedGrid1.setItem(3, 0, Items.LINE_STONE)
        finishedGrid1.setItem(3, 1, Items.LINE_BOTH)
        finishedGrid1.setItem(3, 2, Items.CORNER_1, Orientation.EAST)
        finishedGrid1.setItem(2, 2, Items.FOUR, Orientation.SOUTH)
        finishedGrid1.setItem(2, 1, Items.DRAGON_MUD)
        finishedGrid1.setItem(1, 2, Items.CASTLE_MUD_1, Orientation.EAST)
        finishedGrid1.setItem(2, 3, Items.CORNER_2)
        finishedGrid1.setItem(3, 3, Items.CASTLE_STONE_1, Orientation.WEST)
        finishedGrid1.setItem(9, 3, Items.DRAGON_STONE)

        finishedGrid2 = Grid(15, 5)
        finishedGrid2.heroPos = Pos(3, 5)
        finishedGrid2.setItem(3, 4, Items.CASTLE_STONE_1)

        unfinishedGrid1 = Grid(15, 5)

        unfinishedGrid2 = Grid(15, 5)
        unfinishedGrid2.heroPos = Pos(-1, 2)
        unfinishedGrid2.setItem(0, 2, Items.LINE_STONE)
        unfinishedGrid2.setItem(0, 3, Items.CASTLE_STONE_1, Orientation.SOUTH)

        // Moving through a dragon isn't permitted
        unfinishedGrid3 = Grid(15, 5)
        unfinishedGrid3.heroPos = Pos(-1, 3)
        unfinishedGrid3.setItem(0, 3, Items.THREE_3)
        unfinishedGrid3.setItem(0, 3, Items.CASTLE_MUD_1, Orientation.EAST)
        unfinishedGrid3.setItem(1, 3, Items.DRAGON_STONE)
        unfinishedGrid3.setItem(2, 3, Items.CASTLE_STONE_1)
    }

    @Test
    fun testIsFinished() {
        assertTrue(GridSolver.isFinished(finishedGrid1, 1))
        assertTrue(GridSolver.isFinished(finishedGrid2, 0))
        assertFalse(GridSolver.isFinished(unfinishedGrid1, 0))
        assertFalse(GridSolver.isFinished(unfinishedGrid2, 0))
        assertFalse(GridSolver.isFinished(unfinishedGrid2, 0))
        assertFalse(GridSolver.isFinished(finishedGrid1, 2))
    }
}