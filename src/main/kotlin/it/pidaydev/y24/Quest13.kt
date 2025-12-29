package it.pidaydev.y24

import it.pidaydev.common.Position
import it.pidaydev.common.quest
import java.util.*
import kotlin.math.abs

private val quester = YEAR quest 13 withParser { rows ->
    val (s, e, rest) = List(3) { mutableListOf<Platform>() }
    rows.forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            when (c) {
                'S' -> s += Platform(x, y, 0)
                'E' -> e += Platform(x, y, 0)
                in '0'..'9' -> rest += Platform(x, y, c.digitToInt())
            }
        }
    }
    Maze(s, e, rest)
}

private const val MAX = 100000

private data class Platform(val x: Int, val y: Int, val z: Int, var cost: Int = MAX) :
    Comparable<Platform> {

    val position = Position(x, y)

    fun costTo(other: Platform) = when {
        abs(x - other.x) > 1 -> MAX
        abs(y - other.y) > 1 -> MAX
        else -> 1 + minOf(abs(z - other.z), abs(10 + z - other.z), abs(10 + other.z - z))
    }

    override fun compareTo(other: Platform) =
        (cost - other.cost).takeUnless { it == 0 }
            ?: (z - other.z).takeUnless { it == 0 }
            ?: (x - other.x).takeUnless { it == 0 }
            ?: (y - other.y).takeUnless { it == 0 }
            ?: 0

    override fun toString() = "P[$x,$y,$z]@$cost"
}

private data class Maze(val starts: List<Platform>, val ends: List<Platform>, val platforms: List<Platform>) {
    private val all = platforms + starts + ends
    private val positionToPlatform = all.associateBy { it.position }

    fun minPath(src: Platform = starts.single(), dst: Platform = ends.single()): Int {
        src.cost = 0

        val previous = mutableMapOf(src to src)

        val queue = TreeSet<Platform>()
        queue += all

        while (queue.isNotEmpty()) {
            val curr: Platform = queue.pollFirst()!!
            curr.position
                .around4()
                .mapNotNull { positionToPlatform[it] }
                .forEach { neighbor ->
                    val newCost = curr.costTo(neighbor) + curr.cost
                    if (newCost < neighbor.cost) {
                        queue -= neighbor
                        neighbor.cost = newCost
                        previous[neighbor] = curr
                        queue += neighbor
                    }
                }
        }

        val path = mutableListOf(dst)
        while (true) {
            val last = path.last()
            val prev = previous[last]!!
            if (prev == last) break
            path += prev
        }
        return path.zipWithNext().sumOf { (a, b) -> a.costTo(b) }
    }

    override fun toString() = "Maze(S=$starts, E=$ends)"

}

fun main() {
    fun part1() = quester.read(1).minPath()
    fun part2() = quester.read(2).minPath()
    fun part3(): Int {
        val maze = quester.read(3)
        return maze.starts
            .asSequence()
            .mapNotNull { try { maze.copy(starts = listOf(it)).minPath() } catch (_: Exception) { null } }
            .min()
    }
    quester.printAndVerify(::part1, ::part2, ::part3)
}
