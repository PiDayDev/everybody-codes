package it.pidaydev.story02

import it.pidaydev.common.Position
import it.pidaydev.common.quest

private val quester = STORY quest 3 withParser { lines ->
    val dice = lines
        .takeWhile { it.isNotBlank() }
        .map { line ->
            val id = line.substringBefore(":").toInt()
            val seed = line.substringAfterLast("=").toInt()
            val faces = line.substringAfter("[").substringBefore("]")
                .split(",").map { it.toInt() }
            PolyDie(id, faces, seed)
        }
    val track = lines.takeLastWhile { it.isNotBlank() }
    PolyDieGame(dice, track)
}

private infix fun Long.positiveModulus(m: Int): Int = (((this % m) + m) % m).toInt()

private class PolyDie(val id: Int, val faces: List<Int>, val seed: Int) {
    private var pulse = seed.toLong()
    private var rollNumber = 1
    private var faceIndex = 0

    fun roll(): Int {
        val spin = rollNumber * pulse
        faceIndex = (faceIndex + spin) positiveModulus faces.size
        pulse = (pulse + spin) % seed + 1 + rollNumber + seed
        rollNumber++
        return faces[faceIndex]
    }
}

private class PolyDieGame(val dice: List<PolyDie>, val track: List<String>) {
    private val positions: Map<Int, Set<Position>> by lazy {
        val map = (1..9).associateWith { mutableSetOf<Position>() }
        track.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                map[c.digitToInt()]!! += Position(x, y)
            }
        }
        map
    }

    fun play2(): List<Int> {
        val raceTrack = track.single()
        val indices = dice.associate { it.id to 0 }.toMutableMap()
        val answer = mutableListOf<Int>()
        var i = 0
        while (answer.size < dice.size) {
            i++
            for (die in dice) {
                val result = die.roll()
                val index = indices[die.id]!!
                if (index == raceTrack.length && die.id !in answer) {
                    answer += die.id
                } else if (result == raceTrack.getOrNull(index)?.digitToInt()) {
                    indices[die.id] = index + 1
                }
            }
        }
        return answer.toList()
    }

    fun play3(): Int {
        val reachable = mutableSetOf<Position>()
        dice.forEach { reachable += playDie(it) }
        return reachable.size
    }

    private fun playDie(die: PolyDie): Set<Position> {
        val reached = mutableSetOf<Position>()
        var value = die.roll()
        var current = positions[value]!!
        while (current.isNotEmpty()) {
            reached += current
            val candidates = current + current.flatMap { it.around4() }
            value = die.roll()
            current = positions[value]!!.intersect(candidates)
        }
        return reached
    }

}

fun main() {

    fun part1(): Int {
        val dice = quester.read(1).dice
        var score = 0
        var counter = 0
        while (score < 10000) {
            score += dice.sumOf { it.roll() }
            counter++
        }
        return counter
    }

    fun part2(): String = quester.read(2).play2().joinToString(",")

    fun part3() = quester.read(3).play3()

    quester.printAndVerify(::part1, ::part2, ::part3)
}
