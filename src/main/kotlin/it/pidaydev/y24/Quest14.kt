package it.pidaydev.y24

import it.pidaydev.common.quest
import kotlin.math.max
import kotlin.math.min

private val quester = YEAR quest 14 withRowParser {
    val parts = it.split(",")
    parts.map { s ->
        segments[s.first()]!! * s.drop(1).toInt()
    }
}

private data class Segment(val x: Int = 0, val y: Int = 0, val z: Int = 0) {
    operator fun times(k: Int) = Segment(k * x, k * y, k * z)
    operator fun plus(s: Segment) = Segment(x + s.x, y + s.y, z + s.z)
    operator fun rangeTo(s: Segment): List<Segment> {
        val xs = min(x, s.x)..max(x, s.x)
        val ys = min(y, s.y)..max(y, s.y)
        val zs = min(z, s.z)..max(z, s.z)
        return xs.flatMap { cx ->
            ys.flatMap { cy ->
                zs.map { cz ->
                    Segment(cx, cy, cz)
                }
            }
        }
    }

    fun around6() = listOf(
        copy(x = x - 1), copy(y = y - 1), copy(z = z - 1),
        copy(x = x + 1), copy(y = y + 1), copy(z = z + 1),
    )
}

private val segments = mapOf(
    'U' to Segment(z = +1),
    'D' to Segment(z = -1),
    'R' to Segment(x = +1),
    'L' to Segment(x = -1),
    'F' to Segment(y = +1),
    'B' to Segment(y = -1),
)

fun main() {

    fun List<Segment>.toTree() = runningFold(Segment()) { a, b -> a + b }

    fun part1(): Int {
        val steps = quester.read(1).single()
        return steps.toTree().maxOf { it.z }
    }

    fun part2(): Int {
        val multiSteps: List<List<Segment>> = quester.read(2)
        val segments = multiSteps
            .flatMap { it.toTree().zipWithNext().map { (a, b) -> a..b } }
            .flatten()
            .toSet()
        return (segments - Segment()).size
    }

    fun part3(): Int {
        val trees = quester.read(3).map { it.toTree() }
        val leaves = trees.map { it.last() }
        val segments = trees
            .flatMap { it.zipWithNext().map { (a, b) -> a..b } }
            .flatten()
            .toSet()

        val maxZ = segments.filter { it.x == 0 && it.y == 0 }.maxOf { it.z }

        fun distanceFromTrunk(leaf: Segment, z: Int): Int {
            val goal = Segment(z = z)
            var curr = setOf(leaf)
            var dist = 0
            while (curr.none { it == goal }) {
                curr = curr.flatMap { it.around6() }.filter { it in segments }.toSet()
                dist++
            }
            return dist
        }

        return (0..maxZ).minOf { z ->
            leaves.sumOf { distanceFromTrunk(it, z) }
        }
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
