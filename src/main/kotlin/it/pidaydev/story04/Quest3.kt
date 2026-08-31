package it.pidaydev.story04

import it.pidaydev.common.Position
import it.pidaydev.common.quest
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max

private val quester = STORY quest 3 withParser { lines ->
    val (w, h, ho, vo) = lines.map { it.substringAfter("=") }
    fun String.toInts() = map { it.digitToInt() }
    Floor(w.toInt(), h.toInt(), ho.toInts(), vo.toInts())
}

private enum class Color {
    B, Y;

    fun change() = when (this) {
        B -> Y
        else -> B
    }
}

private data class Floor(
    val width: Int,
    val height: Int,
    val hOffsets: List<Int>,
    val vOffsets: List<Int>,
) {
    fun hasEnclosedTileFrom(topLeft: Position): Boolean {
        val topRight = Position(topLeft.x + 1, topLeft.y)
        val bottomLeft = Position(topLeft.x, topLeft.y + 1)
        return hasDownSegmentFrom(topLeft) &&
                hasRightSegmentFrom(topLeft) &&
                hasDownSegmentFrom(topRight) &&
                hasRightSegmentFrom(bottomLeft)
    }

    private fun hasRightSegmentFrom(p: Position): Boolean {
        val (x, y) = p
        return hOffsets[y % hOffsets.size] == x % 2
    }

    private fun hasDownSegmentFrom(p: Position): Boolean {
        val (x, y) = p
        return vOffsets[x % vOffsets.size] == y % 2
    }

    fun biFloodFill(): Map<Position, Color> {
        val allPositions = mutableSetOf<Position>()
        for (x in 0..<width) {
            for (y in 0..<height) {
                allPositions += Position(x, y)
            }
        }
        val start = Position(0, 0)
        val colors = mutableMapOf<Position, Color>()

        fun floodFill(from: Position, color: Color) {
            val q = LinkedList(listOf(from))
            while (q.isNotEmpty()) {
                val next = q.pollFirst()
                if (next in colors.keys) continue
                colors[next] = color
                allPositions -= next
                val neighbors = next.neighbors().filter { n -> areJoined(n, next) }
                q += neighbors - colors.keys
            }
        }

        floodFill(start, Color.B)

        fun findNextStart(): Pair<Position, Color>? {
            for (pos in allPositions) {
                if (pos !in colors) {
                    val neighborsColors = pos.neighbors().mapNotNull { colors[it] }.toSet()
                    if (neighborsColors.size == 1) {
                        return pos to neighborsColors.single().change()
                    }
                }
            }
            return null
        }

        while (colors.size < width * height) {
            val nextStart = findNextStart() ?: break
            val (position, c) = nextStart
            floodFill(position, c)
        }

        return colors
    }

    private fun Position.neighbors(): List<Position> = around4()
        .filter { it.x in 0..<width }
        .filter { it.y in 0..<height }

    private fun areJoined(p1: Position, p2: Position): Boolean = when {
        p1.x == p2.x && abs(p1.y - p2.y) == 1 ->
            !hasRightSegmentFrom(p1.copy(y = max(p1.y, p2.y)))

        p1.y == p2.y && abs(p1.x - p2.x) == 1 ->
            !hasDownSegmentFrom(p1.copy(x = max(p1.x, p2.x)))

        else -> false
    }

}

fun main() {

    fun part1(): Int {
        val floor = quester.read(1)
        return (0..<floor.height).sumOf { y ->
            (0..<floor.width).count { x ->
                floor.hasEnclosedTileFrom(Position(x, y))
            }
        }
    }

    fun part2() = mostIsolatedTilesOfSameColor(quester.read(2))

    fun part3() = mostIsolatedTilesOfSameColor(quester.read(3))

    quester.printAndVerify(::part1, ::part2, ::part3)
}

/* Yes, it's an ugly mess - I should really refactor and simplify */
private fun mostIsolatedTilesOfSameColor(largeFloor: Floor): Long {
    val floor = largeFloor.let { it.copy(vOffsets = it.vOffsets + it.vOffsets, hOffsets = it.hOffsets + it.hOffsets) }
    val w = floor.vOffsets.size
    val h = floor.hOffsets.size
    val wRest = floor.width % w
    val hRest = floor.height % h
    val wScale = 4 + ceil(floor.width.toDouble() / w.toDouble()).toInt() % 4 - if (wRest > 0) 1 else 0
    val hScale = 4 + ceil(floor.height.toDouble() / h.toDouble()).toInt() % 4 - if (hRest > 0) 1 else 0
    val fragment = floor.copy(width = w * wScale + wRest, height = h * hScale + hRest)
    val colorMap = fragment.biFloodFill()
    val countByZone = mutableMapOf<Position, MutableMap<Color, Int>>()
    (0..<fragment.height).forEach { y ->
        (0..<fragment.width).forEach { x ->
            val pos = Position(x, y)
            val zone = Position(x / w, y / h)
            val zoneCount = countByZone.computeIfAbsent(zone) { colorMap() }
            if (fragment.hasEnclosedTileFrom(pos)) {
                val color = colorMap[pos]!!
                zoneCount[color] = zoneCount[color]!! + 1
            }
        }
    }
    val lastXZone = countByZone.keys.maxOf { it.x }
    val lastYZone = countByZone.keys.maxOf { it.y }
    val countTopLeft = countByZone[Position(0, 0)] ?: colorMap()
    val countTopRight = countByZone[Position(lastXZone, 0)] ?: colorMap()
    val countBottomRight = countByZone[Position(lastXZone, lastYZone)] ?: colorMap()
    val countBottomLeft = countByZone[Position(0, lastYZone)] ?: colorMap()
    val countTop = countByZone[Position(1, 0)] ?: colorMap()
    val countLeft = countByZone[Position(0, 1)] ?: colorMap()
    val countBottom = countByZone[Position(1, lastYZone)] ?: colorMap()
    val countRight = countByZone[Position(lastXZone, 1)] ?: colorMap()
    val countInner = countByZone[Position(1, 1)] ?: colorMap()

    val totals = mutableMapOf<Color, Long>()

    fun addFrom(map: Map<Color, Int>, times: Long = 1L) {
        Color.entries.forEach { color ->
            totals.merge(color, times * (map[color] ?: 0).toLong()) { a, b -> a + b }
        }
    }

    val wRepeat = ((floor.width / w) - 1).toLong()
    val hRepeat = ((floor.height / h) - 1).toLong()
    addFrom(countTopLeft, 1)
    addFrom(countTopRight, 1)
    addFrom(countBottomLeft, 1)
    addFrom(countBottomRight, 1)
    addFrom(countTop, wRepeat)
    addFrom(countLeft, hRepeat)
    addFrom(countBottom, wRepeat)
    addFrom(countRight, hRepeat)
    addFrom(countInner, wRepeat * hRepeat)
    return totals.values.max()
}

private fun colorMap(): MutableMap<Color, Int> = Color.entries.associateWith { 0 }.toMutableMap()
