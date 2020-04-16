package generator
//
//import model.*
//import util.Random
//
//class TreeGridGenerator(private val width: Int, private val height: Int) {
//
//    private lateinit var root: Branch
//
//    fun generate() {
//        val hero = chooseHeroPos()
//        root = Branch(NodeData(hero, null, null))
//    }
//
//    private fun chooseHeroPos(): Pos {
//        val positions = MutableList(width * 2 + height * 2) {
//            when {
//                it < width -> Pos(it, -1)
//                it < width * 2 -> Pos(it - width, height)
//                it < width * 2 + height -> Pos(-1, it - width * 2)
//                else -> Pos(width, it - (width * 2 + height))
//            }
//        }
//        return Random.from(positions)
//    }
//}
//
//class Tree(val grid: Grid) {
//
//    private fun hasGridCastlesAndDragons(): Boolean {
//        val containsMudCastle = grid.any {
//            it.itemState.item == Items.CASTLE_MUD_1 || it.itemState.item == Items.CASTLE_MUD_2 }
//        val containsStoneCastle = grid.any {
//            it.itemState.item == Items.CASTLE_STONE_1 || it.itemState.item == Items.CASTLE_STONE_2 }
//        val contains2Dragons = grid.count { it.itemState.item.type == ItemType.DRAGON } == 2
//        return containsMudCastle && containsStoneCastle && contains2Dragons
//    }
//
//    class Branch(
//            val pos: Pos,
//            val item: ItemState?,
//            val parent: BranchParent? = null,
//            val children: MutableOrientationMap<Branch> = MutableOrientationMap(),
//            var explored: Boolean = false) {
//
//        private fun explore() {
//
//        }
//
//        private fun findPossibleItems(direction: Orientation): List<ItemPossibility2> {
//            // Items have to connect to parent
//        }
//
//        private fun buildBranchForItem(direction: Orientation, item: ItemState): Branch {
//            val itemPos = pos.moveInOrientation(direction)
//            val branch = Branch(itemPos, item, BranchParent(this, direction.opposite()))
//            children[direction] = branch
//            return branch
//        }
//    }
//}
//
//data class BranchParent(val parent: Tree.Branch, val direction: Orientation)
//
//data class ItemPossibility2(
//        val item: Item,
//        val ups: List<Orientation>)
//
