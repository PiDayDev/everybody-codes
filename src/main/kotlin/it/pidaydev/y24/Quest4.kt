package it.pidaydev.y24

import it.pidaydev.common.quest
import kotlin.math.abs

private val quester = YEAR quest 4 withRowParser { it.toLong() }

fun main() {

    fun List<Long>.sumOfDiffFromMin(): Long {
        val min = min()
        return sumOf { it - min }
    }

    fun part1() = quester.read(1).sumOfDiffFromMin()
    fun part2() = quester.read(2).sumOfDiffFromMin()
    fun part3() = quester.read(3).run {
        val median = sorted()[size / 2]
        sumOf { abs(it - median) }
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
