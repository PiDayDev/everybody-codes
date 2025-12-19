package it.pidaydev.y24

import it.pidaydev.common.quest

private val quester = YEAR quest 11 withParser { input ->
    input.associate { row ->
        val (source, dest) = row.split(":")
        val destinations = dest.split(",")
        source to destinations.groupingBy { it }.eachCount()
    }
}

fun main() {

    fun countTermites(part: Int, days: Int, starter: String): Long {
        val edges = quester.read(part)
        val result = (1..days).fold(mapOf(starter to 1L)) { acc, _ ->
            val output = mutableMapOf<String, Long>()
            acc.forEach { (termite, count) ->
                val quantities = edges[termite]
                quantities?.forEach { (nt, q) ->
                    output.merge(nt, count * q) { a, b -> a + b }
                }
            }
            output
        }
        return result.values.sum()
    }

    fun part1() = countTermites(part = 1, days = 4, starter = "A")
    fun part2() = countTermites(part = 2, days = 10, starter = "Z")

    fun part3(): Long {
        val types = quester.read(3).keys
        val counts = types.map { countTermites(part = 3, days =20, starter = it) }
        return counts.max()-counts.min()
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
