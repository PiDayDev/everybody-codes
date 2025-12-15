package it.pidaydev.y24

import it.pidaydev.common.readInput

private const val QUEST = 3

private typealias Grid = List<List<Int>>

private enum class Adjacency { ORTHOGONAL, OMNIDIRECTIONAL }

private fun List<String>.toGrid(): Grid = map { row ->
    row.map { c ->
        when (c) {
            '#' -> 1
            else -> 0
        }
    }
}

private fun Grid.incrementTo(n: Int, adjacency: Adjacency): Grid {
    val prev = n - 1
    return mapIndexed { y, row ->
        row.mapIndexed { x, c ->
            when (c) {
                prev -> {
                    val n = getOrNull(y - 1)?.getOrNull(x)
                    val s = getOrNull(y + 1)?.getOrNull(x)
                    val w = getOrNull(y)?.getOrNull(x - 1)
                    val e = getOrNull(y)?.getOrNull(x + 1)
                    val nw = getOrNull(y - 1)?.getOrNull(x - 1)
                    val ne = getOrNull(y - 1)?.getOrNull(x + 1)
                    val sw = getOrNull(y + 1)?.getOrNull(x - 1)
                    val se = getOrNull(y + 1)?.getOrNull(x + 1)
                    val orthogonal = listOfNotNull(n, s, w, e)
                    val diagonal = listOfNotNull(nw, ne, sw, se)
                    val neighbors = orthogonal +
                            (if (adjacency == Adjacency.OMNIDIRECTIONAL) diagonal else emptyList())
                    val invalid = neighbors.any { it < prev }
                    if (invalid)
                        c
                    else
                        c + 1
                }

                else -> c
            }
        }
    }
}

fun main() {

    fun incrementToMax(input: List<String>, adjacency: Adjacency): Int {
        var t = input.toGrid()
        for (n in 2..1000) {
            val next = t.incrementTo(n, adjacency)
            if (next == t) break
            t = next
        }

        return t.flatten().sum()
    }

    fun part1() = incrementToMax(readInput(YEAR, QUEST, 1), Adjacency.ORTHOGONAL)
    fun part2() = incrementToMax(readInput(YEAR, QUEST, 2), Adjacency.ORTHOGONAL)
    fun part3(): Int {
        val input = readInput(YEAR, QUEST, 3)
        val padRow = List(input.first().length) { '.' }.joinToString("")
        val paddedInput = (listOf(padRow) + input + listOf(padRow)).map { ".$it." }
        return incrementToMax(paddedInput, Adjacency.OMNIDIRECTIONAL)
    }

    println("Part 1: ${part1()}")
    println("Part 2: ${part2()}")
    println("Part 3: ${part3()}")
}
