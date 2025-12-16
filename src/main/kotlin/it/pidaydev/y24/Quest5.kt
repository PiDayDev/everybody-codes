package it.pidaydev.y24

import it.pidaydev.common.quest

private val quester = YEAR quest 5 withParser { strings ->
    val rows = strings.map { row -> row.split(" ").map { it.toInt() } }
    val columns = rows.first().indices.map { j ->
        rows.map { it[j] }
    }
    columns
}

fun main() {
    fun List<List<Int>>.round(column: Int): List<List<Int>> {
        val dancers = map { it.toMutableList() }.toMutableList()
        val clapper = dancers[column % dancers.size].removeFirst()
        val destination = dancers[(column + 1) % dancers.size]
        val clapperMoves = clapper % (2 * destination.size)
        val index = when {
            clapperMoves == 0 -> 1
            clapperMoves <= destination.size -> clapperMoves - 1
            else -> 2 * destination.size + 1 - clapperMoves
        }
        destination.add(index = index, element = clapper)
        return dancers
    }

    fun List<List<Int>>.shout() = map { it.first() }.joinToString("")

    fun part1(): String {
        val start = quester.read(1)
        val final = (0 until 10).fold(start) { dancers, round -> dancers.round(round) }
        return final.shout()
    }

    fun part2(): Long {
        var dancers = quester.read(2)
        val occurrences = mutableMapOf<String, Int>()
        for (round in 0..Int.MAX_VALUE) {
            dancers = dancers.round(round)
            val value = dancers.shout()
            val count = (occurrences[value] ?: 0) + 1
            if (count == 2024)
                return value.toLong() * (1 + round)
            occurrences[value] = count
        }
        throw IllegalStateException("The dance failed")
    }

    fun part3(): Long {
        var dancers = quester.read(3)
        val occurrences = mutableSetOf<String>()
        for (round in 0..1000000) {
            dancers = dancers.round(round)
            val value = dancers.shout()
            occurrences += value
        }
        return occurrences.maxOf { it.toLong() }
    }
    quester.verifyAndPrint(::part1, ::part2, ::part3)
}
