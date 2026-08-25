package it.pidaydev.y25

import it.pidaydev.common.quest

private val quester = YEAR quest 1 withParser { lines ->
    val names = lines.first().split(",")
    val instructions = lines.last().split(",").map {
        val dir = if (it.startsWith("R")) +1 else -1
        val count = it.drop(1).toInt()
        dir * count
    }
    Egg(names, instructions)
}

private class Egg(val names: List<String>, val instructions: List<Int>)

private infix fun Int.positiveModulus(m: Int) = ((this % m) + m) % m

fun main() {

    fun part1(): String {
        val egg = quester.read(1)
        val finalIndex = egg.instructions.fold(0) { index, instruction ->
            (index + instruction).coerceIn(egg.names.indices)
        }
        return egg.names[finalIndex]
    }

    fun part2(): String {
        val egg = quester.read(2)
        val period = egg.names.size
        val finalIndex = egg.instructions.fold(0) { index, instruction ->
            (index + instruction) positiveModulus period
        }
        return egg.names[finalIndex]
    }

    fun part3(): String {
        val egg = quester.read(3)
        val period = egg.names.size
        val names = egg.names.toMutableList()
        egg.instructions.forEach { idx ->
            val j = idx positiveModulus period
            val at0 = names[0]
            val atJ = names[j]
            names[0] = atJ
            names[j] = at0
        }
        return names.first()
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
