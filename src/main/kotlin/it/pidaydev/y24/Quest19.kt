package it.pidaydev.y24

import it.pidaydev.common.Position
import it.pidaydev.common.quest

private val quester = YEAR quest 19 withParser { rows ->
    MessageGrid(rows.first(), rows.drop(2))
}

private class MessageGrid(private val keys: String, rows: List<String>) {
    private val content = rows.map { it.toCharArray() }
    private val width = content.first().size
    private val height = content.size

    override fun toString() = content.joinToString("\n") { it.joinToString("") }

    fun newPosGrid() = PosGrid(width, height)

    fun toStringByPosGrid(posGrid: PosGrid): String {
        val sb = StringBuilder()
        posGrid.getCurrentRows().forEach { row ->
            row.forEach { pos ->
                sb.append(content[pos.y][pos.x])
            }
            sb.appendLine()
        }
        return sb.toString()
    }

    fun toPermutationMatrix(): PermutationMatrix {
        val rows: List<List<Position>> = List(height) { y ->
            List(width) { x ->
                Position(x, y)
            }
        }

        val content = rows.map { it.toMutableList() }.toMutableList()

        fun MutableList<MutableList<Position>>.rotate(y: Int, x: Int, dir: Char) {
            val positions = listOf(
                Position(x - 1, y - 1),
                Position(x, y - 1),
                Position(x + 1, y - 1),
                Position(x + 1, y),
                Position(x + 1, y + 1),
                Position(x, y + 1),
                Position(x - 1, y + 1),
                Position(x - 1, y)
            )
            val start: List<Position> = positions.map { this[it.y][it.x] }

            val next = when (dir) {
                'L' -> start.drop(1) + start.take(1)
                'R' -> start.takeLast(1) + start.dropLast(1)
                else -> throw IllegalArgumentException("$dir")
            }
            positions.zip(next) { pos, c ->
                this[pos.y][pos.x] = c
            }
        }

        fun MutableList<MutableList<Position>>.applyAllRotations() {
            val lastY = lastIndex
            val lastX = first().lastIndex

            fun indexFor(y: Int, x: Int): Int =
                (((y - 1) * (lastX - 1) + (x - 1))) % keys.length

            (1..<lastY).forEach { y ->
                (1..<lastX).forEach { x ->
                    val dir = keys[indexFor(y, x)]
                    rotate(y, x, dir)
                }
            }
        }

        content.applyAllRotations()

        val start = rows.flatten()
        val end = content.flatten()
        val matrix = start.map { end.indexOf(it) }
        return PermutationMatrix(matrix)
    }
}

private class PosGrid(private val width: Int, private val height: Int) {
    private val flattenedPositions = List(height) { y ->
        List(width) { x -> Position(x, y) }
    }.flatten().toMutableList()

    fun getCurrentRows() = flattenedPositions.chunked(width)

    operator fun times(matrix: PermutationMatrix): PosGrid {
        val result = PosGrid(width, height)
        matrix.onesColumns.forEachIndexed { previous, next ->
            val pos = flattenedPositions[previous]
            val dest = flattenedPositions[next]
            result.flattenedPositions[dest.y * width + dest.x] = pos
        }
        return result
    }

}

private data class PermutationMatrix(val onesColumns: List<Int>) {

    operator fun times(other: PermutationMatrix) = PermutationMatrix(onesColumns.map { other.onesColumns[it] })

    private fun identity() = PermutationMatrix(onesColumns.indices.toList())

    fun pow(exp: Int): PermutationMatrix {
        // Exponentiation by squaring
        val squares = mutableListOf(this)
        val max = exp.toBigInteger().bitLength()
        while (squares.size <= max) {
            squares += squares.last() * squares.last()
        }
        return squares.indices.fold(identity()) { result, bitPos ->
            when {
                exp and (1 shl bitPos) != 0 -> result * squares[bitPos]
                else -> result
            }
        }
    }
}

fun main() {

    fun String.extractDecodedMessage(): String =
        replace("""\s""".toRegex(), "").dropWhile { it != '>' }.dropLastWhile { it != '<' }
            .removeSurrounding(">", "<")

    fun solvePart(part: Int, rounds: Int = 1): String {
        val grid = quester.read(part)

        val matrix = grid.toPermutationMatrix()

        val finalPosGrid = grid.newPosGrid() * matrix.pow(rounds)

        val finalGrid = grid.toStringByPosGrid(finalPosGrid)
        println(finalGrid)

        return finalGrid.extractDecodedMessage()
    }

    fun part1() = solvePart(part = 1)
    fun part2() = solvePart(part = 2, rounds = 100)
    fun part3() = solvePart(part = 3, rounds = 1048576000)

    quester.printAndVerify(::part1, ::part2, ::part3)

}

