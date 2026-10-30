package it.pidaydev.y26

import it.pidaydev.common.quest

private val quester = YEAR quest 4 withRowParser { row ->
    val (id, quest, part, time) = row.split(" ")
        .map { it.substringAfter("=").toInt() }
    Result(id, quest, part, time)
}

private data class Result(val id: Int, val quest: Int, val part: Int, val time: Int)

fun main() {
    fun part1() = quester.read(1)
        .groupBy { it.id }
        .values
        .map { it.sumOf { r -> r.time } }
        .sorted()
        .mapIndexed { index, value -> (index + 1) * value }
        .sum()

    fun part2() = quester.read(2)
        .groupBy { it.id }
        .values
        .map { it.count() to it.sumOf { r -> r.time } }
        .sortedWith(
            Comparator
                .comparing<Pair<Int, Int>, Int> { (count) -> -count }
                .thenComparing { (_, time) -> time }
        )
        .mapIndexed { index, (_, time) -> (index + 1) * time }
        .sum()
//        .joinToString("\n")

    fun part3(): Int {
        val results = quester.read(3)
        val totalsByParticipant = mutableMapOf<Int, Int>()

        fun MutableMap<Int, Int>.addScoresFrom(
            questResultsByParticipant: Map<Int, List<Result>>,
            parts: Int,
        ) {
            var previousTime = -1
            var parityCount = 0
            val partialResultsSorted = questResultsByParticipant
                .mapValues { (_, results) -> results.filter { it.part <= parts } }
                .filterValues { it.size == parts }
                .mapValues { (_, results) -> results.sumOf { it.time } }
                .toList()
                .sortedBy { (_, totalTime) -> totalTime }
            partialResultsSorted.forEachIndexed { index, (id, time) ->
                parityCount = if (previousTime == time) parityCount + 1 else 0
                val score = (parts * 50 - index + parityCount).coerceAtLeast(0)
                merge(id, score, Int::plus)
                previousTime = time
            }
        }

        val quests = results.map { it.quest }.toSet().sorted()
        for (quest in quests) {
            val questResultsByParticipant = results.filter { it.quest == quest }.groupBy { it.id }
            (1..3).forEach { parts ->
                totalsByParticipant.addScoresFrom(questResultsByParticipant, parts)
            }
        }

        var lastScore = -1
        var parities = 0
        return totalsByParticipant
            .toList()
            .sortedByDescending { it.second }
            .mapIndexed { index, (id, score) ->
                parities = if (lastScore == score) parities + 1 else 0
                lastScore = score
                (totalsByParticipant[id] ?: 0) * (index + 1 - parities)
            }
            .sum()
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
