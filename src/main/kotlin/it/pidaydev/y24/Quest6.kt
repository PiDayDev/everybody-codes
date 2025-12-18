package it.pidaydev.y24

import it.pidaydev.common.quest

private val quester = YEAR quest 6 withParser { rows ->
    val edges = rows.map { row ->
        val (source, dest) = row.split(":")
        val destinations = dest.split(",")
        source to destinations
    }
    Tree(edges.toMap())
}

private class Tree(val edges: Map<String, List<String>>) {
    fun paths(from: String): List<List<String>> {
        val successors = edges[from]
        if (successors.isNullOrEmpty()) {
            return listOf(listOf(from))
        } else {
            val ends = successors.map { paths(it) }
            val result = mutableListOf<List<String>>()
            ends.forEach { qu: List<List<String>> ->
                result.addAll(qu.map { path ->
                    listOf(from) + path
                })
            }
            return result
        }
    }

    fun backPathsFromApples(): List<List<String>> {
        val ends = edges.filterValues { "@" in it }.keys
        return ends.map { backPathFrom(it) + "@" }
    }

    fun backPathFrom(unique: String): List<String> {
        val predecessor = edges.filterValues { unique in it }.keys.firstOrNull()
            ?: return listOf(unique)
        return backPathFrom(predecessor) + unique
    }
}

fun main() {

    fun uniquePath(questPart: Int, answerAdapter: (String) -> String): String {
        val tree = quester.read(questPart)
        val applePaths = tree.backPathsFromApples()
        val uniquePathLength = applePaths
            .groupingBy { it.size }
            .eachCount()
            .filterValues { it == 1 }
            .keys
            .first()
        return applePaths.first { it.size == uniquePathLength }
            .joinToString(separator = "", transform = answerAdapter)
    }

    fun part1() = uniquePath(1) { it }
    fun part2() = uniquePath(2) { it.take(1) }
    fun part3() = uniquePath(3) { it.take(1) }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
