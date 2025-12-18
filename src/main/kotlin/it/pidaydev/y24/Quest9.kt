package it.pidaydev.y24

import it.pidaydev.common.quest

private val quester = YEAR quest 9 withRowParser { it.toInt() }

fun main() {

    fun optimize(part: Int, stamps: List<Int>, maxTopLevelDifference: Int = stamps.max()): Int {
        val sparkBalls = quester.read(part)
        val maxStamp = stamps.max()

        val cache = stamps.associateWith { 1 }.toMutableMap()

        fun minBugs(goal: Int, enforceMaxDiff: Boolean = false): Int = cache.getOrPut(goal) {
            val start = ((goal - maxStamp) / 2).coerceAtLeast(1)
            val end = (goal / 2).coerceAtMost(goal - 1)
            (start..end).minOf {
                when {
                    enforceMaxDiff && goal - it > it + maxTopLevelDifference -> Int.MAX_VALUE
                    else -> minBugs(it) + minBugs(goal - it)
                }
            }
        }

        return sparkBalls.sumOf { minBugs(goal = it, enforceMaxDiff = true) }
    }

    quester.verifyAndPrint({
        optimize(
            part = 1,
            stamps = listOf(1, 3, 5, 10),
        )
    }, {
        optimize(
            part = 2,
            stamps = listOf(1, 3, 5, 10, 15, 16, 20, 24, 25, 30),
        )
    }, {
        optimize(
            part = 3,
            stamps = listOf(1, 3, 5, 10, 15, 16, 20, 24, 25, 30, 37, 38, 49, 50, 74, 75, 100, 101),
            maxTopLevelDifference = 100
        )
    })
}
