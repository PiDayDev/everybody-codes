package it.pidaydev.y26

import it.pidaydev.common.quest

private val quester = YEAR quest 3 withRowParser {
    val (code, value) = it.split(" ")
    IsoNumber(code.substringAfter("-"), value)
}

private fun Int.pow(exp: Int): Long = when (exp) {
    0 -> 1L
    else -> pow(exp - 1) * this
}

private data class IsoNumber(val isoCode: String, val isoValue: String) {
    fun toLong(): Long = isoCode.reversed().repeat(isoValue.length)
        .zip(isoValue.reversed())
        .map { (a, b) -> a.digitToInt() to b.digitToInt() }
        .mapIndexed { index, (base, digit) -> base.pow(index) * digit }
        .sum()

    fun isValidIso2Number(): Boolean {
        val goal = toLong()
        for (k in 1..<isoValue.toInt()) {
            if (goal == IsoNumber(isoCode, "$k").toLong())
                return false
        }
        return true
    }
}

fun main() {
    fun part1() = quester.read(1).sumOf { it.toLong() }
    fun part2() = quester.read(2).sumOf { it.toLong() }
    fun part3() = quester.read(3).filter { it.isValidIso2Number() }.sumOf { it.toLong() }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
