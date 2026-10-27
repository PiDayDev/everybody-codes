package it.pidaydev.y25

import it.pidaydev.common.quest

private val quester = YEAR quest 5 withRowParser { row ->
    val id = row.substringBefore(":").toInt()
    val values = row.substringAfter(":").split(",").map { it.toInt() }
    values.fold(Sword(id)) { sword, value -> sword + value }
}

private data class Segment(val spine: Int, val left: Int? = null, val right: Int? = null) {
    private val quality = toString().replace("""\D""".toRegex(), "").toInt()

    operator fun plus(n: Int): List<Segment> = when {
        n < spine && left == null -> listOf(copy(left = n))
        n > spine && right == null -> listOf(copy(right = n))
        else -> listOf(this, Segment(n))
    }

    operator fun compareTo(other: Segment) = quality.compareTo(other.quality)

    override fun toString(): String {
        val l = (left?.toString() ?: "").padStart(4)
        val r = (right?.toString() ?: "").padEnd(4)
        val m = spine.toString().padStart(3).padEnd(5)
        return "$l -$m- $r"
    }
}

private data class Sword(val id: Int, val content: List<Segment> = emptyList()) : Comparable<Sword> {
    val quality = content.map { it.spine }.joinToString("").ifBlank { "0" }.toLong()

    operator fun plus(n: Int): Sword {
        when {
            content.isEmpty() -> return copy(content = listOf(Segment(n)))
            else -> {
                for (j in content.indices) {
                    val segment = content[j]
                    val next = segment + n
                    if (next.size == 1) {
                        val newList = content.take(j) + next + content.drop(j + 1)
                        return copy(content = newList)
                    }
                }
                val newTail = content.last() + n
                return copy(content = content.dropLast(1) + newTail)
            }
        }
    }

    override operator fun compareTo(other: Sword): Int {
        val q = quality.compareTo(other.quality)
        if (q != 0) return q
        val segmentPairs = content.zip(other.content)
        for ((a, b) in segmentPairs) {
            val d = a.compareTo(b)
            if (d != 0) return d
        }
        return id.compareTo(other.id)
    }

    override fun toString() = content.joinToString("\n")
}

fun main() {

    fun part1(): Long {
        val sword = quester.read(1).single()
        return sword.quality
    }

    fun part2(): Long {
        val swords = quester.read(2)
        val qualities = swords.map { it.quality }.sortedDescending()
        return qualities.first() - qualities.last()
    }

    fun part3(): Any {
        val swords = quester.read(3)
        val bestToWorst = swords.sortedDescending()
        return bestToWorst
            .zip(1..swords.size){ sword, pos -> sword.id * pos }
            .sum()
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
