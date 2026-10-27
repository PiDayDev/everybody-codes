package it.pidaydev.y26

import it.pidaydev.common.quest
import kotlin.math.sqrt

private val quester = YEAR quest 1 withParser { lines ->
    lines.single().split(",").map { it.toInt() }
}


fun main() {

    fun part1(): Int {
        fun Int.nextSquare(): Int {
            val root = sqrt(toDouble()).toInt()
            return when {
                root * root == this -> this
                else -> (root + 1) * (root + 1)
            }
        }

        val cones = quester.read(1)
        return cones.sumOf { it.nextSquare() - it }
    }

    fun part2(): Int {
        fun Int.nextFrame(): Int {
            val quarter = this / 4
            return when {
                this <= 8 -> 8
                quarter * 4 == this -> this
                else -> (quarter + 1) * 4
            }
        }

        val cones = quester.read(2)
        return cones.sumOf { it.nextFrame() - it }
    }

    fun part3(): Int {
        val cones = quester.read(3)
        val mostCones = cones.max()
        val fortresses = generateSequence(3) { it + 2 }.map { it * it - 1 }.takeWhile { it < mostCones * 2 }.toList()

        fun Int.nextFortress(): Int = fortresses.first { f -> f>=this }

        return cones.sumOf { it.nextFortress() - it }
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
