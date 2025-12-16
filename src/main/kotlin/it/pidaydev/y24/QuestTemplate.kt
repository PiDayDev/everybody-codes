package it.pidaydev.y24

import it.pidaydev.common.quest

private val quester = YEAR quest 9999 withParser {
    // TODO
}

fun main() {

    fun part1() = quester.read(1)
    fun part2() = quester.read(2)
    fun part3() = quester.read(3)

    quester.verifyAndPrint(::part1
//        , ::part2
//        , ::part3
    )
}
