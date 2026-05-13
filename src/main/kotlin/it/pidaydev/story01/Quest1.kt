package it.pidaydev.story01

import it.pidaydev.common.quest
import java.math.BigInteger

typealias BI = BigInteger

private val quester = STORY quest 1 withRowParser { row ->
    val numbers = row.split(" ").map { it.split("=").last().toBigInteger() }
    val (a, b, c) = numbers
    val (x, y, z, m) = numbers.drop(3)
    InputRow(a, b, c, x, y, z, m)
}

private data class InputRow(
    val a: BI, val b: BI, val c: BI,
    val x: BI, val y: BI, val z: BI,
    val m: BI,
) {
    fun applyFormula(eni: (BI, BI, BI) -> BigInteger) =
        eni(a, x, m) + eni(b, y, m) + eni(c, z, m)
}

fun pow(n: BI, exp: BI, mod: BI): BigInteger = n.modPow(exp, mod)

fun <Y> List<Y>.findPeriod(): List<Y> {
    val maxSize = size / 2
    (1..maxSize).forEach { periodSize ->
        val period = takeLast(periodSize)
        if (takeLast(periodSize * 2).take(periodSize) == period) {
            return period
        }
    }
    error("No period found")
}

fun main() {

    fun part1(): BigInteger {
        val input = quester.read(1)

        fun eni(n: BI, exp: BI, mod: BI) = (1..exp.toInt())
            .map { pow(n, it.toBigInteger(), mod) }
            .reversed()
            .joinToString("")
            .toBigInteger()

        return input.maxOf { it.applyFormula(::eni) }
    }

    fun part2(): BigInteger {
        val input = quester.read(2)

        fun eni(n: BI, exp: BI, mod: BI) = (4 downTo 0)
            .map { pow(n, exp - it.toBigInteger(), mod) }
            .reversed()
            .joinToString("")
            .toBigInteger()

        return input.maxOf { it.applyFormula(::eni) }
    }

    fun part3(): BigInteger {
        val input = quester.read(3)

        fun eni(n: BI, exp: BI, mod: BI): BigInteger {
            (1..10).forEach { factor ->
                try {
                    val firstRemainders = (1..mod.toInt() * factor)
                        .map { pow(n, it.toBigInteger(), mod) }

                    val period = firstRemainders.findPeriod()

                    val part1 = firstRemainders.sumOf { it }

                    val (periodOccurrences, rest) =
                        (exp - mod).divideAndRemainder(period.size.toBigInteger())

                    val part2 = periodOccurrences * period.sumOf { it }

                    val part3 = period.take(rest.toInt()).sumOf { it }

                    return part1 + part2 + part3
                } catch (e: Exception) {
                }
            }
            error("No period found")
        }


        val solution = input
            //.maxOf { it.applyFormula(::eni) }
            .map {
                val result = it.applyFormula(::eni)
                println("$it ==> $result")
                result
            }.maxOf { it }

        val wrong = 670903884455076.toBigInteger()
        check(solution != wrong) { "Wrong solution: $solution" }
        check("$solution".length == "$wrong".length) { "Wrong solution length for $solution: ${"$solution".length} != ${"$wrong".length}" }
        check("$solution".take(1) == "$wrong".take(1)) { "Wrong solution prefix: $solution does not start with ${"$wrong".take(5
                )
            }"
        }
        return solution
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
