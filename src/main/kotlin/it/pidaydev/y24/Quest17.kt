package it.pidaydev.y24

import it.pidaydev.common.Position
import it.pidaydev.common.quest

private val quester = YEAR quest 17 withParser { rows ->
    val positions = mutableListOf<Position>()
    rows.forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            if (c == '*') positions += Position(x, y)
        }
    }
    positions.toList()
}

private class DisjointSet<T>(universe: List<T>) {
    private val size = universe.size
    private val parent = universe.associateWith { it }.toMutableMap()
    private val rank = universe.associateWith { 0 }.toMutableMap()
    var count = size
        private set

    fun find(v: T): T {
        var v = v
        while (parent[v] != v) {
            parent[v] = parent[parent[v]]!!
            v = parent[v]!!
        }
        return v
    }

    fun union(v: T, w: T) {
        val rootV = find(v)!!
        val rootW = find(w)!!
        if (rootV == rootW) return
        if (rank[rootV]!! > rank[rootW]!!) {
            parent[rootW] = rootV
        } else if (rank[rootW]!! > rank[rootV]!!) {
            parent[rootV] = rootW
        } else {
            parent[rootV] = rootW
            rank[rootW] = 1 + rank[rootW]!!
        }
        count--
    }

    fun toGroups(): Collection<List<T>> =
        parent.keys.groupBy { find(it) }.values
}

fun main() {

    fun findSizeOfMinConstellation(stars: List<Position>): Int {
        val constellation = stars.take(1).toMutableList()
        val rest = stars.drop(1).toMutableList()

        var total = 0
        while (rest.isNotEmpty()) {
            var best = Int.MAX_VALUE
            var next = rest.first()
            rest.forEach { candidate ->
                constellation.forEach { star ->
                    val d = candidate.manhattan(star)
                    if (d < best) {
                        best = d
                        next = candidate
                    }
                }
            }
            total += best
            constellation += next
            rest -= next
        }
        return total + stars.size
    }

    fun part1() = findSizeOfMinConstellation(quester.read(1))
    fun part2() = findSizeOfMinConstellation(quester.read(2))
    fun part3(): Long {
        val stars = quester.read(3)
        val brilliant = DisjointSet(stars)

        for (from in stars) {
            for (to in stars) {
                if (from.manhattan(to) < 6)
                    brilliant.union(from, to)
            }
        }

        val (first, second, third) = brilliant
            .toGroups()
            .map { findSizeOfMinConstellation(it).toLong() }
            .sortedDescending()

        return first * second * third
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}

