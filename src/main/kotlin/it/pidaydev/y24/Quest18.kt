package it.pidaydev.y24

import it.pidaydev.common.Position
import it.pidaydev.common.quest

private val quester = YEAR quest 18 withParser { rows ->
    val palms = mutableSetOf<Position>()
    val channels = mutableSetOf<Position>()
    rows.forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            val pos = Position(x, y)
            when (c) {
                'P' -> palms += pos
                '.' -> channels += pos
            }
        }
    }
    val origin = channels.filter {
        it.y == 0 || it.y == rows.lastIndex || it.x == 0 || it.x == rows.first().lastIndex
    }.toSet()
    Farm(sources = origin, channels = channels, palms = palms)
}

private data class Farm(
    private val sources: Set<Position>,
    private val channels: Set<Position>,
    private val palms: Set<Position>,
) {
    fun toWorkingFarm() = WorkingFarm(this, irrigated = sources)

    fun toAllWorkingFarmsFromPalms() = palms
        .asSequence()
        .map { palm -> WorkingFarm(this, setOf(palm)) }

    infix fun allowsFlowIn(p: Position) = p in channels || p in palms

    infix fun arePalmsFullyWateredBy(irrigation: Collection<Position>) = irrigation.containsAll(palms)
    infix fun isFullyWateredBy(irrigation: Collection<Position>) = irrigation.containsAll(palms + channels)

    fun isPalm(p: Position) = p in palms
}

private data class WorkingFarm(
    private val farm: Farm,
    private val irrigated: Set<Position>,
    private val k: Int = 0
) {
    var frontier: Set<Position> = irrigated
    fun flow(): WorkingFarm {
        val next = frontier.flatMap { it.around4() }.filter { farm allowsFlowIn it }
        val frontier = (next - irrigated).toSet()
        return copy(irrigated = irrigated + frontier, k = k + 1).apply { this.frontier = frontier }
    }

    fun isWateringAllPalms() = farm arePalmsFullyWateredBy irrigated
    fun isWateringEverything() = farm isFullyWateredBy irrigated

    fun sumDistancesForCurrentFrontier(cumulativeMap: MutableMap<Position, Int>) {
        frontier
            .filterNot(farm::isPalm)
            .forEach { pos ->
                cumulativeMap.merge(pos, k) { a, b -> a + b }
            }
        }
    }

    fun main() {

        fun part1(): Int {
            val farm = quester.read(1).toWorkingFarm()
            return generateSequence(farm) { it.flow() }.takeWhile { !it.isWateringAllPalms() }.count()
        }

        fun part2(): Int {
            val farm = quester.read(2).toWorkingFarm()
            return generateSequence(farm) { it.flow() }.takeWhile { !it.isWateringAllPalms() }.count()
        }

        fun part3(): Int {
            val farm = quester.read(3)

            /*
            For part 3 we add together the distances of empty spaces away from every palm tree,
             then look for the cell location with the smallest total.
             */
            val progress = mutableMapOf<Position, Int>()

            farm.toAllWorkingFarmsFromPalms().forEach { workingFarm ->
                generateSequence(workingFarm) { it.flow() }
                    .onEach { it.sumDistancesForCurrentFrontier(progress) }
                    .takeWhile { !it.isWateringEverything() }
                    .count()
            }

            val best = progress.entries
                .minOf { it.value }

            return best
        }

        quester.printAndVerify(::part1, ::part2, ::part3)
    }
