package it.pidaydev.y24

import it.pidaydev.common.readInput

private const val QUEST = 2

fun main() {

    fun part1(): Int {
        val input = readInput(YEAR, QUEST, 1)

        val runes = input.first().substringAfter(":").split(",")
        val inscription = input.last()

        val countByRune = runes.map { rune ->
            inscription.windowed(rune.length).count { it == rune }
        }

        return countByRune.sum()
    }

    fun part2(): Int {
        val input = readInput(YEAR, QUEST, 2)

        val runes = input.first().substringAfter(":").split(",")
        val doubleSidedRunes = (runes + runes.map { it.reversed() }).distinct()

        val inscriptions = input.drop(2)

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
        val input = readInput(YEAR, QUEST, 3)

        val runes = input.first().substringAfter(":").split(",")
        val doubleSidedRunes = (runes + runes.map { it.reversed() }).distinct()

        val inscriptions = input.drop(2)
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

        matchedSymbols.forEach { row ->
            println(row.joinToString("") { if (it) "#" else "." })
        }

        return matchedSymbols.sumOf { row -> row.count { it } }
    }

    println("Part 1: ${part1()}")
    println("Part 2: ${part2()}")
    println("Part 3: ${part3()}")
}
