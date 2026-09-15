package it.pidaydev.story03

import it.pidaydev.common.Direction
import it.pidaydev.common.Position
import it.pidaydev.common.quest

private val quester = STORY quest 2 withParser { lines ->
    val origin = mutableSetOf<Position>()
    val bones = mutableSetOf<Position>()
    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, ch ->
            when (ch) {
                '@' -> origin += Position(x, y)
                '#' -> bones += Position(x, y)
            }
        }
    }
    Beak(origin.single(), bones)
}

private data class Beak(val origin: Position, val bones: Set<Position>) {
    fun stepsToSurroundBones(moves: Iterator<Direction>, log: Boolean = false): Int {
        val occupied = (bones + origin).toMutableSet()
        val goal = bones.flatMap { it.around4() }.toSet() - bones
        var current = origin
        var count = 0
        (occupied.flatMap { it.around4() }.toSet() - occupied).forEach {
            occupied += tryFloodFill(it, occupied)
        }
        while (goal.any { it !in occupied }) {
            count++
            var next = current + moves.next()
            while (next in occupied) {
                next = current + moves.next()
            }
            occupied += next
            current = next

            val floodCandidates = current.around4()
                .filter { it !in occupied }
            floodCandidates.forEach { occupied += tryFloodFill(it, occupied) }
            if (log) {
                println("------ Current ($count) = $current ------")
                print(occupied, current)
            }
        }
        return count
    }

    fun print(positions: Set<Position>, current: Position) {
        val maxX = positions.maxOf { it.x }
        val maxY = positions.maxOf { it.y }
        val minX = positions.minOf { it.x }
        val minY = positions.minOf { it.y }
        (minY..maxY).forEach { y ->
            println((minX..maxX).joinToString("") { x ->
                when (Position(x, y)) {
                    origin -> "*"
                    in bones -> "#"
                    current -> "@"
                    in positions -> "+"
                    else -> " "
                }
            })
        }
    }
}


fun main() {
    fun moves1(): Iterator<Direction> = generateSequence { 1 }
        .flatMap { sequenceOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT) }
        .iterator()

    fun moves3(): Iterator<Direction> = generateSequence { 1 }
        .flatMap {
            List(3) { Direction.UP } + List(3) { Direction.RIGHT } + List(3) { Direction.DOWN } + List(3) { Direction.LEFT }
        }
        .iterator()

    fun part1(): Int {
        val beak = quester.read(1)
        val moves = moves1()
        val occupied = mutableSetOf(beak.origin)
        var current = beak.origin
        while (current !in beak.bones) {
            var next = current + moves.next()
            while (next in occupied) {
                next = current + moves.next()
            }
            occupied += next
            current = next
        }
        return (occupied - beak.bones).size
    }

    fun part2() = quester.read(2).stepsToSurroundBones(moves = moves1())

    fun part3() = quester.read(3).stepsToSurroundBones(moves = moves3())

    quester.printAndVerify(::part1, ::part2, ::part3)
}

private fun tryFloodFill(from: Position, occupied: Set<Position>): Set<Position> {
    val region = mutableSetOf(from)
    val xRange = occupied.map { it.x }.sorted().run { first()..last() }
    val yRange = occupied.map { it.y }.sorted().run { first()..last() }
    while (true) {
        val additions = region.flatMap { it.around4() } - region - occupied
        when {
            additions.isEmpty() -> break
            additions.any { it.x !in xRange || it.y !in yRange } -> return emptySet()
            else -> region += additions
        }
    }
    return region
}
