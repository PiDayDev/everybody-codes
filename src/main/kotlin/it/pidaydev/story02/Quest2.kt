package it.pidaydev.story02

import it.pidaydev.common.quest

private val quester = STORY quest 2 withParser {
    // TODO
}

fun main() {

    fun part1() = quester.read(1)
    fun part2() = quester.read(2)
    fun part3() = quester.read(3)

    quester.printAndVerify(::part1
//        , ::part2
//        , ::part3
    )
}
