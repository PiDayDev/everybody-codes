package it.pidaydev.y25

import it.pidaydev.common.quest

private val quester = YEAR quest 2 withParser { rows ->
    val (_, a, b) = rows.single().split("""[=\]\[,]""".toRegex()).filter { it.isNotBlank() }
    Complex(a.toLong(), b.toLong())
}

private data class Complex(val a: Long, val b: Long) {
    operator fun plus(c: Complex) = Complex(a = a + c.a, b = b + c.b)
    operator fun minus(c: Complex) = Complex(a = a - c.a, b = b - c.b)
    operator fun div(c: Complex) = Complex(a = a / c.a, b = b / c.b)
    operator fun times(c: Complex) = Complex(a = a * c.a - b * c.b, b = a * c.b + b * c.a)
    override fun toString() = "[$a,$b]"
}

fun main() {

    fun part1(): Complex {
        val seed = quester.read(1)
        val result = (1..3).fold(Complex(0, 0)) { acc, _ ->
            (acc * acc) / Complex(10, 10) + seed
        }
        return result
    }

    fun shouldEngrave(c: Complex): Boolean {
        var position = Complex(0, 0)
        val validRange = -1_000_000..1_000_000
        repeat(100) {
            position = (position * position) / Complex(100000, 100000) + c
            if (position.a !in validRange || position.b !in validRange)
                return false
        }
        return true
    }

    fun countEngravings(startPoint: Complex, coordinateIncrements: Iterable<Long>): Int {
        var count = 0
        for (dy in coordinateIncrements) {
            for (dx in coordinateIncrements) {
                val point = startPoint + Complex(dx, dy)
                if (shouldEngrave(point))
                    count++
            }
        }
        return count
    }

    fun part2() = countEngravings(quester.read(2), 0..1000L step 10)

    fun part3() = countEngravings(quester.read(3), 0..1000L)

    quester.printAndVerify(::part1, ::part2, ::part3)
}
