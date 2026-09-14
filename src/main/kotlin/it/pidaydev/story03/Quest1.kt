package it.pidaydev.story03

import it.pidaydev.common.quest
import kotlin.math.max

private val quester = STORY quest 1 withRowParser {
    val (id, r, g, b, s) = it.split("[: ]".toRegex()) + listOf("")
    Scale(id, r, g, b, s)
}

private fun String.toColorComponent() = when {
    isBlank() -> 0
    else -> this
        .replace("[A-Z]".toRegex(), "1")
        .replace("[a-z]".toRegex(), "0")
        .toInt(2)
}

private class Scale(val id: Int, val r: Int, val g: Int, val b: Int, val shine: Int) {
    constructor(id: String, r: String, g: String, b: String, shine: String) : this(
        id.toInt(),
        r.toColorComponent(),
        g.toColorComponent(),
        b.toColorComponent(),
        shine.toColorComponent()
    )
}

fun main() {

    fun part1(): Int = quester.read(1)
        .filter { it.g > max(it.r, it.b) }
        .sumOf { it.id }

    fun part2(): Int {
        val scales = quester.read(2)
        val maxShine = scales.maxOf { it.shine }
        val darkest = scales
            .filter { it.shine == maxShine }
            .minBy { it.r + it.g + it.b }
        return darkest.id
    }

    fun part3(): Int {
        val scales = quester.read(3)
        val redMatte = mutableSetOf<Int>()
        val redShiny = mutableSetOf<Int>()
        val greenMatte = mutableSetOf<Int>()
        val greenShiny = mutableSetOf<Int>()
        val blueMatte = mutableSetOf<Int>()
        val blueShiny = mutableSetOf<Int>()
        scales.forEach { scale ->
            val isMatte = scale.shine <= 30
            val isShiny = scale.shine >= 33
            val isR = scale.r > max(scale.g, scale.b)
            val isG = scale.g > max(scale.r, scale.b)
            val isB = scale.b > max(scale.g, scale.r)
            when {
                isR && isMatte -> redMatte += scale.id
                isR && isShiny -> redShiny += scale.id
                isG && isMatte -> greenMatte += scale.id
                isG && isShiny -> greenShiny += scale.id
                isB && isMatte -> blueMatte += scale.id
                isB && isShiny -> blueShiny += scale.id
            }
        }
        return listOf(redMatte, redShiny, greenMatte, greenShiny, blueMatte, blueShiny)
            .maxBy { it.size }
            .sum()
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
