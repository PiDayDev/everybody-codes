package it.pidaydev.story04

import it.pidaydev.common.quest

private val quester = STORY quest 1 withRowParser { line ->
    line.split(",").map { it.toInt() }.let(::Recaman)
}

private class Recaman(val increments: List<Int>) {
    fun toPointsBasic(): List<Int> {
        val points = mutableListOf(0)
        val seen = mutableSetOf(0)
        increments.forEach { step ->
            val nextNeg = points.last() - step
            val nextPos = points.last() + step
            val nextPoint = if (nextNeg > 0 && nextNeg !in seen) nextNeg else nextPos
            points += nextPoint
            seen += nextPoint
        }
        return points
    }

    fun toPointsDistinct(): List<Int> {
        val points = mutableListOf(0)
        val seen = mutableSetOf(0)
        increments.forEach { step ->
            val nextNeg = points.last() - step
            var nextPos = points.last() + step
            while (nextPos in seen) nextPos++
            val nextPoint = if (nextNeg > 0 && nextNeg !in seen) nextNeg else nextPos
            points += nextPoint
            seen += nextPoint
        }
        return points
    }

    fun toPointsPlanar(): List<Int> {
        val points = mutableListOf(0)
        val seen = mutableSetOf(0)
        val jumps = mutableListOf<Jump>()
        var isUp = false
        val limit = increments.sum() + 1
        increments.forEach { step ->
            val from = points.last()
            val nextNeg = from - step
            val jumpNeg = Jump(nextNeg..from, isUp)
            val canGoBack = nextNeg > 0 && nextNeg !in seen && jumps.none { it.crosses(jumpNeg) }
            if (canGoBack) {
                points += nextNeg
                seen += nextNeg
                jumps += jumpNeg
                isUp = !isUp
            } else {
                var nextPos = from + step
                var jumpPos = Jump(from..nextPos, isUp)
                while (nextPos in seen || jumps.any { it.crosses(jumpPos) }) {
                    nextPos++
                    jumpPos = Jump(from..nextPos, isUp)
                    if (nextPos > limit) break
                }
                if (nextPos <= limit) {
                    points += nextPos
                    seen += nextPos
                    jumps += jumpPos
                    isUp = !isUp
                }
            }
        }
        return points
    }
}

private data class Jump(val range: IntRange, val isUp: Boolean) {
    fun crosses(other: Jump) = isUp == other.isUp &&
            (range.first() in other.range != range.last() in other.range)
}

fun main() {

    fun part1() = quester.read(1).sumOf { it.toPointsBasic().last() }

    fun part2() = quester.read(2).sumOf { it.toPointsDistinct().last() }

    fun part3() = quester.read(3).sumOf {
        val planar = it.toPointsPlanar()
        planar.last()
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
