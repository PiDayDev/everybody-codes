package it.pidaydev.story01

import it.pidaydev.common.Position
import it.pidaydev.common.quest

private val quester = STORY quest 3 withRowParser {
    val (x, y) = it.split(" ")
        .map { c -> c.substringAfter("=").toInt() }
    Position(x, y)
}

private val upRight = Position(+1, -1)

private fun List<Position>.move(days: Int) = map { it.move(days) }

private fun List<Position>.isAligned() = all { it.y == 1 }

private fun Position.move(days: Int): Position {
    val period = x + y - 1
    val delta = upRight * (days % period)
    val goal = this + delta
    return when {
        goal.y >= 1 -> goal
        else -> goal + upRight * -period
    }
}


fun main() {

    fun part1(): Int {
        fun Position.snailScore() = x + 100 * y

        val snails = quester.read(1)

        return snails.move(100).sumOf { it.snailScore() }
    }

    fun part2(): Int {
        val snails = quester.read(2)
        val days = generateSequence(snails) { it.move(1) }
            .takeWhile { !it.isAligned() }
            .count()
        return days
    }

    fun part3(): Long {
        val snails = quester.read(3)
        val periods = snails.map { it.x + it.y - 1 }
        val remainders = snails.map { it.y - 1 }
        return chineseRemainder(periods, remainders)
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}

fun chineseRemainder(periods: List<Int>, remainders: List<Int>): Long {
    val prod = periods.fold(1L) { a, b -> a * b }
    var sum = 0L
    for ((period, remainder) in periods.zip(remainders)) {
        val p = prod / period
        sum += remainder * modInv(p, period.toLong()) * p
    }
    return sum % prod
}

/* returns x where (a * x) % b == 1 */
fun modInv(a: Long, b: Long): Long {
    if (b == 1L) return 1L
    var a0 = a
    var b0 = b
    var x0 = 0L
    var x1 = 1L
    while (a0 > 1L) {
        val q = a0 / b0
        var t = b0
        b0 = a0 % b0
        a0 = t
        t = x0
        x0 = x1 - q * x0
        x1 = t
    }
    if (x1 < 0L) x1 += b
    return x1
}
