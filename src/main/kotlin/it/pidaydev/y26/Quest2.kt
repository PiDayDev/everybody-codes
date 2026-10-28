package it.pidaydev.y26

import it.pidaydev.common.quest

private val quester = YEAR quest 2 withRowParser { row -> Graph.fromString(row) }

private class Graph(
    val nodes: MutableSet<Int> = mutableSetOf(),
    val edges: MutableMap<Int, Set<Int>> = mutableMapOf()
) {
    companion object {
        fun fromString(row: String) = Graph().also { graph ->
            row.split(" ").forEach { graph.addArc(it) }
        }
    }

    fun checksum() = dagOrder().joinToString("").toLong()

    private fun addArc(s: String) {
        val (l, r) = s.split("""\W""".toRegex()).map { it.toInt() }
        nodes += l
        nodes += r
        val (from, to) = when {
            '<' in s -> r to l
            else -> l to r
        }
        edges.merge(from, setOf(to)) { a, b -> a + b }
    }

    private fun dagOrder(): List<Int> {
        if (nodes.isEmpty()) return emptyList()
        val allDestinations = edges.values.flatten().toSet()
        val root = (nodes - allDestinations).single()
        val reducedGraph = Graph((nodes - root).toMutableSet(), (edges - root).toMutableMap())
        return listOf(root) + reducedGraph.dagOrder()
    }
}

fun main() {

    fun part1() = quester.read(1).sumOf { it.checksum() }
    fun part2() = quester.read(2).sumOf { it.checksum() }
    fun part3() = quester.read(3).sumOf { it.checksum() }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
