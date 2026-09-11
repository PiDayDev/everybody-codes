package it.pidaydev.story02

import it.pidaydev.common.Direction
import it.pidaydev.common.Position
import it.pidaydev.common.quest

private val quester = STORY quest 1 withParser { lines ->
    val machine = Machine(lines.takeWhile { it.isNotBlank() })
    val tokens = lines.takeLastWhile { it.isNotBlank() }.map(::Token)
    machine to tokens
}

class Machine(private val lines: List<String>) {
    private val indices = lines.first().indices

    fun slotNumbers(): IntRange {
        val maxSlot = toSlotNumber(Position(indices.last, 0))
        return 1..maxSlot
    }

    fun score(token: Token, initialSlot: Int): Int {
        val finalSlot = finalSlot(token, initialSlot)
        return score(initialSlot, finalSlot)
    }

    private fun toSlotNumber(p: Position): Int = when (p.x % 2) {
        0 -> p.x / 2 + 1
        else -> throw IllegalStateException("$p")
    }

    private fun toPosition(slot: Int) = Position(x = (slot - 1) * 2, y = -1)

    private fun finalSlot(token: Token, initialSlot: Int): Int {
        val seq = token.directions.iterator()
        var pos = toPosition(initialSlot)
        while (!isAtTheBottom(pos)) {
            pos += Direction.DOWN
            if (hasNail(pos)) {
                pos = move(pos, seq.next())
            }
        }
        return toSlotNumber(pos)
    }

    private fun score(from: Int, to: Int): Int =
        (2 * to - from).coerceAtLeast(0)

    private fun hasNail(p: Position): Boolean =
        lines.getOrNull(p.y)?.getOrNull(p.x) == '*'

    private fun move(p: Position, d: Direction): Position =
        when ((p + d).x) {
            in indices -> p + d
            else -> p - d
        }

    private fun isAtTheBottom(p: Position): Boolean =
        p.y >= lines.size
}

data class Token(private val behavior: String) {
    val directions = behavior.map { if (it == 'R') Direction.RIGHT else Direction.LEFT }
}

fun main() {

    fun part1(): Int {
        val (machine, tokens) = quester.read(1)
        val total = tokens.mapIndexed { index, token ->
            val slot = index + 1
            machine.score(token, slot)
        }.sum()
        return total
    }

    fun part2(): Int {
        val (machine, tokens) = quester.read(2)
        val slots = machine.slotNumbers()
        val total = tokens.sumOf { token ->
            slots.maxOf { slot ->
                machine.score(token, slot)
            }
        }
        return total
    }

    fun part3(): String {
        val (machine, tokens) = quester.read(3)
        val slots = machine.slotNumbers()
        val possibilities = tokens.map { token ->
            slots.map { slot ->
                machine.score(token, slot)
            }
        }

        val (t1, t2, t3) = possibilities
        val (t4, t5, t6) = possibilities.takeLast(3)

        fun List<Int>.minMax() = sorted().run { listOf(first(), last()) }

        val scores: List<Int> =
            t1.indices.flatMap { i1 ->
                (t2.indices - i1).flatMap { i2 ->
                    (t3.indices - i1 - i2).flatMap { i3 ->
                        (t4.indices - i1 - i2 - i3).flatMap { i4 ->
                            (t5.indices - i1 - i2 - i3 - i4).flatMap { i5 ->
                                (t6.indices - i1 - i2 - i3 - i4 - i5).map { i6 ->
                                    t1[i1] + t2[i2] + t3[i3] + t4[i4] + t5[i5] + t6[i6]
                                }.minMax()
                            }.minMax()
                        }.minMax()
                    }.minMax()
                }.minMax()
            }.minMax()

        return "${scores.min()} ${scores.max()}"
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
