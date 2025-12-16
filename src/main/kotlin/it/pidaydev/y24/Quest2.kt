package it.pidaydev.y24

import it.pidaydev.common.quest

private data class RuneExam(val runes: List<String>, val inscriptions: List<String>)

private val quester = YEAR quest 2 withParser {
    RuneExam(
        runes = it.first().substringAfter(":").split(","),
        inscriptions = it.drop(2)
    )
}

fun main() {

    fun part1(): Int {
        val (runes, inscriptions) = quester.read(1)
        val inscription = inscriptions.first()
        val countByRune = runes.map { rune ->
            inscription.windowed(rune.length).count { it == rune }
        }
        return countByRune.sum()
    }

    fun part2(): Int {
        val (runes, inscriptions) = quester.read(2)
        val doubleSidedRunes = (runes + runes.map { it.reversed() }).distinct()

        val symbols = inscriptions.sumOf { inscription ->
            val ranges = mutableListOf<IntRange>()
            inscription.indices.forEach { j ->
                val sub = inscription.drop(j)
                doubleSidedRunes.forEach { rune ->
                    if (sub.startsWith(rune))
                        ranges.add(j until j + rune.length)
                }
            }
            ranges.fold(emptySet<Int>()) { a, b -> a + b }.size
        }

        return symbols
    }

    fun part3(): Int {
        val (runes, inscriptions) = quester.read(3)
        val doubleSidedRunes = (runes + runes.map { it.reversed() }).distinct()
        val width = inscriptions.first().length
        val height = inscriptions.size
        val matchedSymbols = List(height) { BooleanArray(width) { false } }

        // horizontals
        inscriptions.forEachIndexed { y, inscription ->
            val wrapped = inscription + inscription
            doubleSidedRunes.forEach { rune ->
                rune.toRegex().findAll(wrapped).forEach { matchResult ->
                    matchResult.range.forEach { x ->
                        matchedSymbols[y][x % width] = true
                    }
                }
            }
        }

        // verticals
        inscriptions.first().indices.forEach { x ->
            val column = inscriptions.joinToString("") { row -> "${row[x]}" }
            doubleSidedRunes.forEach { rune ->
                rune.toRegex().findAll(column).forEach { matchResult ->
                    matchResult.range.forEach { y ->
                        matchedSymbols[y][x % width] = true
                    }
                }
            }
        }

        return matchedSymbols.sumOf { row -> row.count { it } }
    }

    quester.verifyAndPrint(::part1, ::part2, ::part3)
}
