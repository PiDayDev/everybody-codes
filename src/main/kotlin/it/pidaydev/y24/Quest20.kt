package it.pidaydev.y24

import it.pidaydev.common.Direction
import it.pidaydev.common.Position
import it.pidaydev.common.quest

private const val START = 'S'

private val quester = YEAR quest 20 withParser { rows ->
    val letters = mutableMapOf<Char, Position>()
    val none = mutableSetOf<Position>()
    val cold = mutableSetOf<Position>()
    val warm = mutableSetOf<Position>()
    rows.forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            val p = Position(x, y)
            when (c) {
                in 'A'..'Z' -> {
                    letters[c] = p
                    none += p
                }

                '.' -> none += p
                '-' -> cold += p
                '+' -> warm += p
            }
        }
    }
    FiniteGliderMap(letters, none, cold, warm)
}

private interface GliderMap {
    operator fun get(pos: Position): AirCurrent?
    fun getCheckpoint(pos: Position): Char?
}

private data class FiniteGliderMap(
    val checkpoints: Map<Char, Position> = emptyMap(),
    val currents: Map<Position, AirCurrent>
) : GliderMap {
    constructor(checkpoints: Map<Char, Position>, none: Set<Position>, cold: Set<Position>, warm: Set<Position>) : this(
        checkpoints,
        none.associateWith { AirCurrent.NONE } + cold.associateWith { AirCurrent.COLD } + warm.associateWith { AirCurrent.WARM }
    )

    val start = checkpoints[START] ?: error("No starting point found")

    override operator fun get(pos: Position) = currents[pos]

    override fun getCheckpoint(pos: Position): Char? = checkpoints.entries.firstOrNull { it.value == pos }?.key

    fun startPlanes(altitude: Int) = Direction.entries.map { Plane(start, it, altitude) }
}

private data class InfiniteGliderMap(val baseMap: FiniteGliderMap) : GliderMap {
    private val height = baseMap.currents.keys.maxOf { it.y } + 1

    override operator fun get(pos: Position): AirCurrent? = when {
        pos.y < 0 -> null
        else -> baseMap[Position(pos.x, pos.y % height)]
    }

    override fun getCheckpoint(pos: Position): Char? = null
}

private enum class AirCurrent(val deltaAltitude: Int) {
    NONE(-1), COLD(-2), WARM(+1)
}

private data class Plane(
    val position: Position,
    val direction: Direction,
    val altitude: Int,
    val visitedCheckpoints: String = "$START"
) {
    fun nextStates(
        map: GliderMap,
        directions: List<Direction> = listOf(direction.turnLeft(), direction, direction.turnRight())
    ): List<Plane> {
        return directions.mapNotNull { dir ->
            val nextPos = position + dir
            val checkpoint = map.getCheckpoint(nextPos)?.toString() ?: ""
            when (val airCurrent = map[nextPos]) {
                null -> null
                else -> Plane(
                    position = nextPos,
                    direction = dir,
                    altitude = altitude + airCurrent.deltaAltitude,
                    visitedCheckpoints = visitedCheckpoints + checkpoint
                )
            }
        }
    }
}

fun main() {

    fun part1(): Int {
        val map = quester.read(1)
        val start = map.startPlanes(1_000)

        fun Collection<Plane>.optimize(): List<Plane> {
            val byPos = groupBy { it.position to it.direction }
            return byPos.values.map { group -> group.maxBy { it.altitude } }
        }

        val candidates = (0..<100).fold(start) { planes, seconds ->
            planes.flatMap { it.nextStates(map) }.optimize()
        }
        return candidates.maxOf { it.altitude }
    }

    fun part2(): Int {
        val map = quester.read(2)
        val goalAltitude = 10_000
        val start = map.startPlanes(goalAltitude)
        val expectedCheckpoints = "SABCS"

        fun Collection<Plane>.filterAndOptimize(): List<Plane> {
            val filtered = filter { expectedCheckpoints.startsWith(it.visitedCheckpoints) }
                .filter { it.altitude > 0 }
                .filterNot { it.position == map.start && it.altitude < goalAltitude }
            val byPos = filtered.groupBy { Triple(it.position, it.direction, it.visitedCheckpoints) }
            return byPos.values.map { group -> group.maxBy { it.altitude } }
        }

        fun Collection<Plane>.winner(): Plane? =
            firstOrNull { it.position == map.start && it.visitedCheckpoints == expectedCheckpoints }

        (generateSequence(0) { it + 1 }).fold(start) { planes, seconds ->
            planes.winner()?.let { return seconds }
            if (seconds % 100 == 0) println(
                "After $seconds seconds: ${planes.size} planes | ${
                    planes.groupingBy { it.visitedCheckpoints }.eachCount()
                }"
            )
            planes.flatMap { it.nextStates(map) }.filterAndOptimize()
        }
        return -1
    }

    fun part3(): Int {
        val map = quester.read(3)
        val startAltitude = 384_400
        val start = map.startPlanes(startAltitude)
            .filterNot { it.direction == Direction.UP }
        val infiniteMap = InfiniteGliderMap(map)

        fun Collection<Plane>.farthest(): Plane? = maxByOrNull { it.position.y }

        fun Collection<Plane>.filterAndOptimize(): List<Plane> {
            val filtered = filter { it.altitude >= 0 }
            val byPos = filtered.groupBy { it.position to it.direction }
            val candidates = byPos.values.map { group -> group.maxBy { it.altitude } }
            val highest = candidates.sortedByDescending { it.altitude }.take(20)
            val farthest = candidates.sortedByDescending { it.position.y }.take(20)
            return (highest + farthest).distinct()
        }

        var best = 0
        generateSequence(start to 1) { (planes, seconds) ->
            if (seconds % 10000 == 0) println("At second $seconds we have ${planes.size} planes | BEST=$best")
            val farthest = planes.farthest() ?: return@generateSequence null
            best = best.coerceAtLeast(farthest.position.y)
            planes.flatMap {
                it.nextStates(
                    infiniteMap,
                    listOf(Direction.DOWN, Direction.LEFT, Direction.RIGHT)
                        .intersect(setOf(it.direction, it.direction.turnRight(), it.direction.turnLeft()))
                        .toList()
                )
            }.filterAndOptimize() to seconds + 1
        }.lastOrNull()

        return best
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
