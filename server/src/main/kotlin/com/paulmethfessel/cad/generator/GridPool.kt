package com.paulmethfessel.cad.generator

import com.paulmethfessel.cad.model.Grid
import com.paulmethfessel.cad.server.Server
import com.paulmethfessel.cad.util.shutdownSafelyNow
import org.slf4j.MarkerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

typealias NewGridListener = (Grid) -> Unit

private val tag = MarkerFactory.getMarker("GridPool")

const val POOL_SIZE = 100

object GridPool {

    private val pool = ConcurrentLinkedQueue<Grid>()
    private val newGridListeners = ConcurrentLinkedQueue<NewGridListener>()

    private val getGridLock = ReentrantLock()
    private val addGridLock = ReentrantLock()

    private val generators = Executors.newWorkStealingPool()

    fun run() {
        generateNewGrid(POOL_SIZE)
    }

    fun getNewGrid(): Grid? {
        getGridLock.withLock {
            if (pool.isEmpty()) {
                return null
            }

            val grid = pool.remove()
            Server.log.info(tag, "Removed. size=${pool.size}")
            generateNewGrid()
            return grid
        }
    }

    fun notifyOnNewGrid(listener: NewGridListener) {
        newGridListeners.add(listener)
    }

    fun close() {
        generators.shutdownSafelyNow()
    }

    private fun generateNewGrid(times: Int = 1) {
        val neededGrids = maxOf(POOL_SIZE - pool.size, 0)
        repeat(minOf(neededGrids, times)) {
            generators.execute {
                val grid = RecursiveGenerator(Server.config.gridWidth, Server.config.gridHeight).generate()
                onNewGrid(grid)
                Server.log.info(tag, "Added. size=${pool.size}")
            }
        }
    }

    private fun onNewGrid(grid: Grid) {
        addGridLock.withLock {
            if (newGridListeners.isNotEmpty()) {
                // If listeners are waiting for new grids
                val listener = newGridListeners.remove()
                listener(grid)
            } else {
                pool.add(grid)
            }
        }
    }
}