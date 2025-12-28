package it.pidaydev.common

import java.io.File

/** Reads lines from the given input txt file. */
private fun readInput(year: Int, quest: Int, part: Int) = File(
    "src/main/resources/inputs/${year}",
    "everybody_codes_e${year}_q${quest.toString().padStart(2, '0')}_p${part}.txt"
).readLines()

infix fun Int.quest(quest: Int) = Quest(this, quest)

data class Quest(val year: Int, val number: Int) {
    infix fun <T> withParser(fn: (List<String>) -> T) = Quester(this, fn)
    infix fun <R> withRowParser(fn: (String) -> R) = withParser { list: List<String> -> list.map(fn) }
}

data class Quester<T>(val quest: Quest, val parse: (List<String>) -> T) {
    fun read(part: Int) = parse(readInput(quest.year, quest.number, part))

    fun printAndVerify(part1: () -> Any = {}, part2: () -> Any = {}, part3: () -> Any = {}) {
        val expected = readSolutions(quest.year, quest.number)
        val v1 = part1()
        if (v1 != Unit) {
            print("Part 1: $v1")
            expected.getOrNull(0)?.let {
                println(if ("$v1" == it) " ✅" else " ❌ Expected $it, got $v1")
            } ?: println()
        }
        val v2 = part2()
        if (v2 != Unit) {
            print("Part 2: $v2")
            expected.getOrNull(1)?.let {
                println(if ("$v2" == it) " ✅" else " ❌ Expected $it, got $v2")
            } ?: println()
        }
        val v3 = part3()
        if (v3 != Unit) {
            print("Part 3: $v3")
            expected.getOrNull(2)?.let {
                println(if ("$v3" == it) " ✅" else " ❌ Expected $it, got $v3")
            } ?: println()
        }
    }
}

private fun readSolutions(year: Int, quest: Int) = try {
    File(
        "src/main/resources/solutions/${year}",
        "e${year}_q${quest.toString().padStart(2, '0')}.txt"
    )
        .readLines()
        .map { it.substringAfter(":").trim() }
} catch (_: Exception) {
    emptyList()
}

data class Position(val x: Int, val y: Int) {
    operator fun plus(p: Position) = Position(x + p.x, y + p.y)
    operator fun times(k: Int) = Position(k * x, k * y)
    override fun toString() = "($x,$y)"
}
