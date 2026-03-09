package it.pidaydev.y24

import it.pidaydev.common.quest

private val quester = YEAR quest 16 withParser { rows ->
    val steps = rows.first().split(",").map { it.toInt() }

    val catRows = rows.drop(2)
    val wheels = steps.mapIndexed { j, step ->
        val cats = catRows
            .map { row -> row.drop(j * 4).take(3).trim() }
            .filter { it.isNotBlank() }
        Wheel(cats, step)
    }
    Machine(wheels)
}

private data class Machine(
    val wheels: List<Wheel>
) {
    fun catsAt(times: Long) = wheels.joinToString(" ") { it.catAt(times) }

    fun coinsAt(times: Number) = wheels
        .map { it.catAt(times.toLong()) }
        .joinToString("") { "${it.first()}${it.last()}" }
        .groupingBy { it }
        .eachCount()
        .values
        .sumOf { (it - 2).coerceAtLeast(0) }
        .toLong()

    fun stepOne(adjust: Int): Machine = Machine(
        wheels.map {
            it.copy(startingDelta = (it.startingDelta + adjust + it.stepSize) % it.cats.size)
        }
    )
}

private data class Wheel(
    val cats: List<String>,
    val stepSize: Int,
    val startingDelta: Int = 0
) {
    fun catAt(times: Long): String {
        val size = cats.size
        val baseIndex = ((times * stepSize) % size).toInt()
        val index = ((baseIndex + startingDelta) % size + size) % size
        return cats[index]
    }
}

private data class State(
    val machine: Machine,
    val totalCoins: Long
) {
    fun nextStates() = (-1..1).map {
        val nextMachine = machine.stepOne(it)
        State(machine = nextMachine, totalCoins = totalCoins + nextMachine.coinsAt(0))
    }
}

private fun gcd(a: Int, b: Int): Int = when (b) {
    0 -> a
    else -> gcd(b, a % b)
}

private fun lcm(numbers: Collection<Int>): Int = numbers.reduce { a, b -> a * b / gcd(a, b) }

fun main() {

    fun part1(): String {
        val machine = quester.read(1)
        return machine.catsAt(100)
    }

    fun part2(): Long {
        val machine = quester.read(2)

        val period = lcm(machine.wheels.map { it.cats.size })
        val coinsAtFirstPeriod = (1..period).sumOf { machine.coinsAt(it) }

        val final = 202420242024L

        val periodRepeats = final / period
        val exactPeriodsIndex = periodRepeats * period
        val coinsFromRepeats = periodRepeats * coinsAtFirstPeriod

        val lastMile = exactPeriodsIndex + 1..final
        val coinsFromLastMile = lastMile.sumOf { machine.coinsAt(it) }

        return coinsFromRepeats + coinsFromLastMile
    }

    fun part3(): String {
        val machine = quester.read(3)
        val initial = State(machine, 0L)
        val result = (1..256).fold(listOf(initial)) { states, _ ->
            states.flatMap { it.nextStates() }.distinct()
        }

        return result.map { it.totalCoins }.let { "${it.max()} ${it.min()}" }
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
