package it.pidaydev.y25

import it.pidaydev.common.quest
import kotlin.math.ceil

private val quester = YEAR quest 4 withRowParser { it.toInt() }
private val quester3 = YEAR quest 4 withParser { rows ->
    rows
        .map { row -> row.split("|").map { it.toInt() } }
        .map(::GearGroup)
}

private data class GearGroup(val teeth: List<Int>) {
    val firstTeeth = teeth.first()
    val ratio = teeth.last()/teeth.first()

}

fun main() {

    fun part1() = quester.read(1).run {
        first() * 2025 / last()
    }

    fun part2() = quester.read(2).run {
        ceil(1e13 * last() / first()).toLong()
    }

    fun part3(): Long {
        val gearGroups = quester3.read(3)
        val first = gearGroups.first()
        val last = gearGroups.last()
        val totalRatio = gearGroups.drop(1).dropLast(1).fold(1L) {acc,group -> acc*group.ratio}
        return totalRatio * 100* first.firstTeeth / last.firstTeeth
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
