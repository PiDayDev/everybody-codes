package it.pidaydev.y24

import it.pidaydev.common.quest

private val quester = YEAR quest 1 withParser { it.joinToString("") }

fun main() {

    fun potions(monster: Char) = when (monster) {
        'A' -> 0
        'B' -> 1
        'C' -> 3
        'D' -> 5
        else -> 0
    }

    fun solveForGroupsOfSize(size: Int): Int {
        val monsterGroups = quester.read(size).chunked(size)
        return monsterGroups.sumOf { group ->
            val potions = group.sumOf { potions(it) }
            val monsterCount = group.count { it != 'x' }
            val extra = monsterCount * (monsterCount - 1)
            potions + extra
        }
    }

    quester.printAndVerify(
        part1 = { solveForGroupsOfSize(1) },
        part2 = { solveForGroupsOfSize(2) },
        part3 = { solveForGroupsOfSize(3) }
    )
}
