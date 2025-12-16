package it.pidaydev.y24

import it.pidaydev.common.quest
import kotlin.math.sqrt

private val quester = YEAR quest 8 withParser {
    it.first().toLong()
}

fun main() {

    fun part1(): Long {
        val blocks = quester.read(1)
        val prevHeight = sqrt(blocks.toDouble()).toInt()
        val nextHeight = prevHeight + 1
        val finalSize = nextHeight * nextHeight
        val finalWidth = nextHeight * 2 - 1
        return (finalSize - blocks) * finalWidth
    }

    fun part2(): Long {
        val priests = quester.read(2).toInt()
        val acolytes = 1111
        val blocks = 20240000L
        val layerToThickness = mutableMapOf(1 to 1)
        fun getThickness(layer: Int): Int =
            layerToThickness.getOrPut(layer) { (getThickness(layer - 1) * priests) % acolytes }

        var layer = 1
        var totalBlocks = 1L
        while (totalBlocks <= blocks) {
            layer++
            val thickness = getThickness(layer)
            val width = 2 * layer - 1
            val added = thickness * width
            totalBlocks += added
        }

        return (totalBlocks - blocks) * (2 * layer - 1)
    }


    fun part3(): Long {
        val highPriests = quester.read(3).toInt()
        val highAcolytes = 10
        val blocks = 202400000L
        val layerToThickness = mutableMapOf(1 to 1)
        fun getThickness(layer: Int): Int =
            layerToThickness.getOrPut(layer) {
                (getThickness(layer - 1) * highPriests) % highAcolytes + highAcolytes
            }

        fun Map<Int, Long>.totalWithReflection() = values.sum() * 2 - getOrDefault(1, 0)

        var layer = 1
        val columnToHeight = mutableMapOf(1 to 1L)
        while (true) {
            layer++
            val thickness = getThickness(layer)
            columnToHeight[layer] = 0
            columnToHeight.keys.forEach { columnToHeight[it] = columnToHeight[it]!! + thickness }
            val width = 2 * layer - 1

            val removed = mutableMapOf(layer to 0L)
            columnToHeight.forEach { (col, height) ->
                if (col !in removed) {
                    val empty = (highPriests.toLong() * width * height) % highAcolytes
                    removed[col] = empty
                }
            }

            val totalBlocks = columnToHeight.totalWithReflection() - removed.totalWithReflection()
            if (totalBlocks > blocks) {
                return totalBlocks - blocks
            }
        }
    }

    quester.verifyAndPrint(::part1, ::part2, ::part3)
}
