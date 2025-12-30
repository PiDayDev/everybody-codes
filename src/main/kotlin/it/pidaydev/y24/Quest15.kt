package it.pidaydev.y24

import it.pidaydev.common.Position
import it.pidaydev.common.quest
import java.util.*
import kotlin.system.measureTimeMillis

private val quester = YEAR quest 15 withParser { Forest(it) }

const val PATH = '.'
const val INVALID = '#'
const val LAKE = '~'

private data class Forest(val rows: List<String>) {
    val startPos = Position(y = 0, x = rows.first().indexOfFirst { it.isValid() })
    val plants = rows.joinToString("").filter { it.isValid() && it != PATH }.toSet()

    operator fun get(x: Int, y: Int) = rows.getOrNull(y)?.getOrNull(x) ?: '#'
    operator fun get(p: Position) = get(p.x, p.y)
}

private data class Bouquet(val plants: List<Char>, val content: BitSet = BitSet()) {
    fun isComplete() = content.cardinality() == plants.size

    operator fun plus(plant:Char): Bouquet {
        val index = plants.indexOf(plant)
        if (index<0) return this

        val new = content.clone() as BitSet
        new.set(index)
        return copy(content = new)
    }

    fun containsAll(b: Bouquet): Boolean {
        val v =(b.content.clone() as BitSet)
            v.andNot(this.content)
        return v.cardinality()==0
    }
}

private fun Char.isValid() = this != INVALID && this != LAKE

private fun Set<Bouquet>.simplify(): Set<Bouquet> {
    val q = mutableSetOf<Bouquet>()
    val ss = sortedByDescending { it.content.cardinality() }
    ss.forEach { set ->
        if (q.none { it.containsAll(set) })
            q += set
    }
    return q
}

fun main() {

    fun addPlant(
        plant: Char,
        oldBouquets: Set<Bouquet>,
        next: MutableMap<Position, Set<Bouquet>>,
        position: Position
    ) {
        if (plant.isValid()) {
            val newSets = oldBouquets.map { set -> set + plant }.toSet()
            next.merge(position, newSets) { a, b -> a + b }
        }
    }

    fun fastestWayToGetAllPlants(forest: Forest): Int {
        val expectedPlants = forest.plants.toList().sorted()
        var curr = mapOf(forest.startPos to setOf(Bouquet(expectedPlants)))
        var dist = 0

        while (true) {
            dist++
            val next = mutableMapOf<Position, Set<Bouquet>>()
            curr.forEach { (pos, oldSets) ->
                val neighbors = pos.around4()
                neighbors.forEach { n ->
                    addPlant(forest[n], oldSets, next, n)
                }
            }
            if (next.any { (pos, plants) -> pos == forest.startPos && plants.any { it.isComplete() } }) {
                return dist
            }
            curr = next
            if (dist % 100 == 0) {
                println("Distance $dist has a map of size ${curr.size} / $expectedPlants")
                curr = curr.mapValues { (_, value) -> value.simplify() }
            }
        }
    }


    fun part1() = fastestWayToGetAllPlants(quester.read(1))
    fun part2() = fastestWayToGetAllPlants(quester.read(2))
    // WARNING: VERY SLOW
    fun part3() = fastestWayToGetAllPlants(quester.read(3))

    quester.printAndVerify(::part1, ::part2, ::part3)
}
