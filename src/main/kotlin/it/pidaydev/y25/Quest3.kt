package it.pidaydev.y25

import it.pidaydev.common.quest

private val quester = YEAR quest 3 withParser { rows ->
    rows.single().split(",").map { it.toInt() }
}

fun main() {

    fun part1() = quester.read(1).toSet().sum()
    fun part2() = quester.read(2).distinct().sorted().take(20).sum()
    fun part3() = quester.read(3).groupingBy { it }.eachCount().values.max()

    quester.printAndVerify(::part1, ::part2, ::part3)
}
