package it.pidaydev.story04

import it.pidaydev.common.Position
import it.pidaydev.common.quest

private val quester = STORY quest 2 withParser { lines ->
    fun String.toCoords() = substringAfter("[")
        .substringBefore("]")
        .split(",")
        .map { it.toInt() }
        .let { Position(it.first(), it.last()) }

    val (start, a, b, c) = lines.take(4).map { it.toCoords() }
    val moves = lines.last().substringAfter("=")
    SparkPlan(start, a, b, c, moves)
}

private data class SparkPlan(
    val start: Position,
    val a: Position,
    val b: Position,
    val c: Position,
    val moves: CharSequence
) {
    private fun beacon(k: Char) = when (k) {
        'A' -> a
        'B' -> b
        else -> c
    }

    fun illuminateOnce(): Set<Position> {
        val illuminated = mutableSetOf(start)
        moves.fold(start) { current, beaconId ->
            val next = current mid beacon(beaconId)
            illuminated += next
            next
        }
        return illuminated
    }

    fun illuminateAll(limit: Int): Set<Position> {
        val illuminated = mutableSetOf(start)
        (1..limit).fold(setOf(start)) { currents, _ ->
            val next = currents
                .flatMap { listOf(a mid it, b mid it, c mid it) }
                .toSet()
                .minus(illuminated)
            illuminated += next
            next
        }
        return illuminated
    }

}

private infix fun Position.mid(other: Position) =
    Position((x + other.x) / 2, (y + other.y) / 2)

fun Collection<Position>.countSurroundingFireflies() =
    (flatMap { it.around4() }.toSet() - toSet()).size

fun main() {

    fun part1() = quester.read(1).illuminateOnce().size
    fun part2() = quester.read(2).illuminateOnce().countSurroundingFireflies()
    fun part3() = quester.read(3).illuminateAll(1000).countSurroundingFireflies()
    quester.printAndVerify(::part1, ::part2, ::part3)
}
