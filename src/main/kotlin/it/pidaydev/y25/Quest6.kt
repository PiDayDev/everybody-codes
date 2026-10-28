package it.pidaydev.y25

import it.pidaydev.common.quest

private val quester = YEAR quest 6 withRowParser {
    it.toCharArray()
}

fun main() {

    fun List<Char>.countPairingsWithPreviousMentors(): Int = (1..size)
        .map { take(it) }
        .sumOf { sub ->
            if (sub.last().isUpperCase()) 0
            else sub.dropLast(1).count { it.isUpperCase() }
        }

    fun part1(): Int {
        val sequence = quester.read(1).single()
        val swordsmen = sequence.filter { it in "Aa" }
        return swordsmen.countPairingsWithPreviousMentors()
    }

    fun part2(): Int {
        val sequence = quester.read(2).single()
        val swordsmen = sequence.filter { it in "Aa" }
        val archers = sequence.filter { it in "Bb" }
        val wizards = sequence.filter { it in "Cc" }
        return listOf(swordsmen, archers, wizards).sumOf { it.countPairingsWithPreviousMentors() }
    }

    fun countPairingsInRange(s: String, range: IntRange, position: Int): Long {
        val letter = s[position]
        return when {
            letter.isUpperCase() -> 0L
            else -> {
                val min = (range.first + position).coerceAtLeast(0)
                val max = (range.last + position).coerceAtMost(s.lastIndex)
                s.substring(min..max).count { c -> c == letter.uppercaseChar() }.toLong()
            }
        }
    }

    fun part3(): Long {
        val sequence = quester.read(3).single()
        val string1 = sequence.joinToString("")
        val string3 = string1.repeat(3)
        val marginSize = string1.length/2
        val repetitions = 1000
        val range = -1000..1000
        val leftTot = (0..marginSize).sumOf { countPairingsInRange(string1, range, it) }
        val rightTot = (marginSize+1..string1.lastIndex).sumOf { countPairingsInRange(string1, range, it ) }
        val centerTot = string1.indices.sumOf { countPairingsInRange(string3, range, it + string1.length) }
        return leftTot + (repetitions - 1) * centerTot + rightTot
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
