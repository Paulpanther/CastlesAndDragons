package com.paulmethfessel.cad.solver

import com.paulmethfessel.cad.model.*

class GridSolver(private val grid: Grid) {

    companion object {
        fun isFinished(grid: Grid, hardness: Int) = GridSolver(grid).isFinished(hardness)
    }

    private fun findAllConnections(pos: ItemState, found: Set<ItemState> = setOf(pos)): Set<ItemState> {
        val connections = getConnectedNeighbors(pos)
        val newConnections = connections.filter { it !in found }
        val allFound = (found + newConnections).toMutableSet()
        for (newConnection in newConnections) {
            allFound.addAll(findAllConnections(newConnection, allFound))
        }
        return allFound
    }

    private fun isFinished(hardness: Int): Boolean {
        if (!heroStartPosConnectedToHero()) {
            return false
        }

        val castles = getPositionsForType(ItemType.CASTLE).all { isConnected(grid.heroStartPos(), it) }
        val anyDragon = getPositionsForType(ItemType.DRAGON).any { isConnected(grid.heroStartPos(), it) }
        val allDragons = getPositionsForType(ItemType.DRAGON).all { isConnected(grid.heroStartPos(), it) }
        return when (hardness) {
            0 -> castles
            1 -> castles && anyDragon
            else -> castles && allDragons
        }
    }

    private fun isConnected(p1: Pos, p2: Pos): Boolean {
        val all = findAllConnections(grid[p1])
        return all.contains(grid[p2])
    }

    private fun heroStartPosConnectedToHero(): Boolean {
        val state = grid[grid.heroStartPos()]
        val orientation = grid.heroStartPos().whichMoveOrientation(grid.heroPos)
        return if (state.item != Items.EMPTY) {
            state.streetAt(orientation) != StreetType.NONE
        } else {
            false
        }
    }

    private fun getConnectedNeighbors(state: ItemState): List<ItemState> {
        return if (state.item.type == ItemType.DRAGON) {
            listOf()
        } else {
            state.getConnectedNeighbors()
        }
    }

    private fun getPositionsForType(type: ItemType): List<Pos> {
        return grid
                .filter { it.itemState.item.type == type }
                .map { Pos(it.x, it.y) }
    }
}