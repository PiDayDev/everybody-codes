package it.pidaydev.y24

import it.pidaydev.common.quest

private val quester = YEAR quest 3 withRowParser {
    it.map { c ->
        when (c) {
            '#' -> 1
            else -> 0
        }
    }
}

private typealias Grid = List<List<Int>>

private enum class Adjacency { ORTHOGONAL, OMNIDIRECTIONAL }

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

    fun incrementToMax(input: Grid, adjacency: Adjacency): Int {
        var t = input
        for (n in 2..1000) {
            val next = t.incrementTo(n, adjacency)
            if (next == t) break
            t = next
        }

        return t.flatten().sum()
    }

    fun part1() = incrementToMax(quester.read(1), Adjacency.ORTHOGONAL)
    fun part2() = incrementToMax(quester.read(2), Adjacency.ORTHOGONAL)
    fun part3(): Int {
        val input = quester.read(3)
        val padRow = List(input.first().count()) { 0 }
        val paddedInput = (listOf(padRow) + input + listOf(padRow)).map { listOf(0) + it + 0 }
        return incrementToMax(paddedInput, Adjacency.OMNIDIRECTIONAL)
    }

    quester.verifyAndPrint(::part1, ::part2, ::part3)
}
