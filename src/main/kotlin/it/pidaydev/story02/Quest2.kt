package it.pidaydev.story02

import it.pidaydev.common.quest

private val quester = STORY quest 2 withParser {
    it.first()
}

private class BalloonLine(val balloons: String) {
    fun pop(): Int {
        val bolts = bolts().iterator()
        val remainingBalloonsSequence = generateSequence(balloons) { remaining ->
            val bolt = bolts.next()
            remaining.dropWhile { c -> c == bolt }.drop(1)
        }
            .takeWhile { it.isNotEmpty() }
        return remainingBalloonsSequence.count()
    }
}

private class BalloonRing(val balloons: String, val times: Int) {
    fun pop(): Int {
        val bolts = bolts().iterator()
        val length = balloons.length
        val totalLength = length * times
        var index1 = 0
        var index2doubled = totalLength
        var count = 0
        val removed = mutableSetOf<Int>()
        while (index1 < totalLength) {
            if (index1 * 2 !in removed) {
                val balloon = balloons[index1 % length]
                if (balloon == bolts.next() && index2doubled % 2 == 0) {
                    removed += index2doubled
                    index2doubled++
                }
                index2doubled++
                count++
            }
            index1++
        }
        return count
    }
}

private fun bolts() = generateSequence { "RGB".asSequence() }.flatten()

fun main() {

    fun part1() = BalloonLine(quester.read(1)).pop()

    fun part2() = BalloonRing(quester.read(2), 100).pop()

    fun part3() = BalloonRing(quester.read(3), 100000).pop()

    quester.printAndVerify(::part1, ::part2, ::part3)
}
