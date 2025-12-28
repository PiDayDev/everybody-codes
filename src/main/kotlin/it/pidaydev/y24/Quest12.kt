package it.pidaydev.y24

import it.pidaydev.common.Position
import it.pidaydev.common.quest

private val quester = YEAR quest 12 withParser { rows ->
    val targets = mutableListOf<Position>()
    var a = Position(0, 0)
    rows.forEachIndexed { y, row ->
        row.forEachIndexed { x, k ->
            val p = Position(x, y)
            when (k) {
                'A' -> a = p
                'T' -> targets.add(p)
                'H' -> targets.addAll(List(2) { p })
                else -> {}
            }
        }
    }
    ShootingRange(
        targets = targets.map { (x, y) -> Position(x - a.x, a.y - y) },
        maxX = rows.first().lastIndex
    )
}

private val quester3 = YEAR quest 12 withParser { rows ->
    val meteors = rows.map { row ->
        val (x, y) = row.split(" ").map { it.toInt() }
        Position(x, y)
    }
    ShootingRange(
        targets = meteors,
        maxX = meteors.maxOf { it.x }
    )
}

private data class ShootingRange(
    val targets: List<Position>,
    val maxX: Int
) {
    companion object {
        val A = Position(0, 0)
        val B = Position(0, 1)
        val C = Position(0, 2)
        val RIGHT_UP = Position(+1, +1)
        val RIGHT = Position(+1, 0)
        val RIGHT_DOWN = Position(+1, -1)
        val LEFT_DOWN = Position(-1, -1)
    }

    fun shootA(power: Int) = shootToFixedTargets(A, power)
    fun shootB(power: Int) = shootToFixedTargets(B, power)
    fun shootC(power: Int) = shootToFixedTargets(C, power)

    private fun shootToFixedTargets(from: Position, power: Int): List<Position> {
        val set = trackShot(from, power).toSet()
        return targets.filter { it in set }
    }

    fun trackShot(from: Position, power: Int): Sequence<Position> {
        var curr = from
        return sequence {
            repeat(power) { curr += RIGHT_UP; yield(curr) }
            repeat(power) { curr += RIGHT; yield(curr) }
            while (curr.y > 0) {
                curr += RIGHT_DOWN; yield(curr)
            }
        }
    }

}

private data class Source(val segmentNumber: Int, val power: Int) {
    val ranking = segmentNumber * power
}

fun main() {

    fun ranking(range: ShootingRange): Int {
        var total = 0
        val targets = range.targets.toMutableList()
        for (power in 1..range.maxX) {
            val aTargets = range.shootA(power)
            val bTargets = range.shootB(power)
            val cTargets = range.shootC(power)
            total += power * (aTargets.size + 2 * bTargets.size + 3 * cTargets.size)
            (aTargets + bTargets + cTargets).forEach { targets.remove(it) }
            if (targets.isEmpty()) break
        }
        return total
    }

    fun part1() = ranking(quester.read(1))

    fun part2() = ranking(quester.read(2))

    // WARNING: very slow
    fun part3(): Long {
        val meteors = quester3.read(3)

        val shots = mutableMapOf<Source, List<Position>>()
        val inverse = mutableMapOf<String, MutableList<Source>>()

        val powerRange = 1..meteors.maxX / 2
        for (power in powerRange) {
            val s1 = Source(1, power)
            val s2 = Source(2, power)
            val s3 = Source(3, power)

            val p1 = meteors.trackShot(ShootingRange.A, power).toList()
            val p2 = meteors.trackShot(ShootingRange.B, power).toList()
            val p3 = meteors.trackShot(ShootingRange.C, power).toList()

            shots[s1] = p1
            shots[s2] = p2
            shots[s3] = p3

            p1.forEach { p -> inverse.getOrPut("$p") { mutableListOf() }.add(s1) }
            p2.forEach { p -> inverse.getOrPut("$p") { mutableListOf() }.add(s2) }
            p3.forEach { p -> inverse.getOrPut("$p") { mutableListOf() }.add(s3) }
        }

        var total = 0L
        meteors.targets.forEach { meteor ->
            meteor@ for (t in 0..meteor.y) {
                val current = meteor + ShootingRange.LEFT_DOWN * t
                val potentialSources = inverse["$current"] ?: emptyList()
                val hits = potentialSources.filter { src -> current in shots[src]!!.subList(0, t) }
                if (hits.isNotEmpty()) {
                    val best = hits.minOf { it.ranking }
                    println("$meteor => $hits ==> $best")
                    total += best
                    break@meteor
                }
            }
        }

        return total
    }

    quester.printAndVerify(::part1, ::part2)
    quester3.printAndVerify(part3 = ::part3)

}
