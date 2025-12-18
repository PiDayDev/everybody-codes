package it.pidaydev.y24

import it.pidaydev.common.quest

private val quester = YEAR quest 10 withParser { it }

private val validRunes = 'A'..'Z'

private val Char.isValid: Boolean
    get() = this in validRunes

private data class Engraving(val symbols: List<String>) {
    override fun toString() = symbols.joinToString("\n")

    private val rows = symbols.subList(2, 6)
    private val innerRows = rows.map { it.subSequence(2..<6) }
    private val outerRows = rows.map { it.take(2) + it.takeLast(2) }
    private val cols = (2..<6).map { x -> symbols.map { row -> row[x] }.joinToString("") }
    private val innerCols = cols.map { it.subSequence(2..<6) }
    private val outerCols = cols.map { it.take(2) + it.takeLast(2) }

    val currentRunicWord = innerRows.joinToString("")

    fun findRunicWord(): String {
        val word = StringBuilder()
        outerRows.forEach { row ->
            outerCols.forEach { col ->
                word.append(col.toSet().intersect(row.toSet()).first())
            }
        }
        return "$word"
    }

    private fun withRunicWord(s: CharSequence) = Engraving(
        symbols.mapIndexed { y, row ->
            when (y) {
                in 2..<6 -> row.mapIndexed { x, ch ->
                    when (x) {
                        in 2..<6 -> s[(y - 2) * 4 + (x - 2)]
                        else -> ch
                    }
                }.joinToString("")

                else -> row
            }
        }
    )

    private fun withInner(c: Char, relativeY: Int, relativeX: Int): Engraving =
        replaceOne(2 + relativeY, 2 + relativeX, c)

    private fun withOuterRow(c: Char, relativeY: Int, relativeX: Int): Engraving {
        val actualY = 2 + relativeY
        val actualX = if (relativeX in 0..1) relativeX else relativeX + 4
        return replaceOne(actualY, actualX, c)
    }

    private fun withOuterCol(c: Char, relativeY: Int, relativeX: Int): Engraving {
        val actualY = if (relativeY in 0..1) relativeY else relativeY + 4
        val actualX = 2 + relativeX
        return replaceOne(actualY, actualX, c)
    }

    private fun replaceOne(absoluteY: Int, absoluteX: Int, c: Char): Engraving = Engraving(
        symbols.mapIndexed { y, row ->
            when (y) {
                absoluteY -> row.mapIndexed { x, k ->
                    when (x) {
                        absoluteX -> c
                        else -> k
                    }
                }.joinToString("")

                else -> row
            }
        }
    )

    fun solveUnknowns(): Engraving {
        if (currentRunicWord.any { it in "-|" }) return this

        val rowRunes = outerRows.joinToString("").filter { it.isValid }
        if (rowRunes.count() != rowRunes.toSet().count()) return withRunicWord("-".repeat(16))

        val colRunes = outerCols.joinToString("").filter { it.isValid }
        if (colRunes.count() != colRunes.toSet().count()) return withRunicWord("|".repeat(16))

        if ('?' !in (outerRows + outerCols).joinToString("")) {
            val word = findRunicWord()
            return withRunicWord(word)
        }

        val word = StringBuilder()
        outerRows.forEach { row ->
            outerCols.forEach { col ->
                val c = col.toSet().intersect(row.toSet()).firstOrNull() ?: '.'
                word.append(c)
            }
        }
        return withRunicWord(word).fillInTheBlanks()
    }

    fun fillInTheBlanks(): Engraving {
        var result = this
        result.innerRows.zip(result.outerRows).forEachIndexed { y, (int, ext) ->
            val x = int.indexOf('.')
            if (x >= 0 && int.count { it.isValid } == 3 && ext.all { it.isValid }) {
                val c = ext.toSet() - int.toSet()
                result = result.withInner(c.single(), y, x)
            }
        }
        result.innerCols.zip(result.outerCols).forEachIndexed { x, (int, ext) ->
            val y = int.indexOf('.')
            if (y >= 0 && int.count { it.isValid } == 3 && ext.all { it.isValid }) {
                val c = ext.toSet() - int.toSet()
                result = result.withInner(c.single(), y, x)
            }
        }
        result.innerRows.zip(result.outerRows).forEachIndexed { y, (int, ext) ->
            val x = ext.indexOf('?')
            if (x >= 0 && ext.count { it.isValid } == 3 && int.all { it.isValid }) {
                val c = int.toSet() - ext.toSet()
                result = result.withOuterRow(c.single(), y, x)
            }
        }
        result.innerCols.zip(result.outerCols).forEachIndexed { x, (int, ext) ->
            val y = ext.indexOf('?')
            if (y >= 0 && ext.count { it.isValid } == 3 && int.all { it.isValid }) {
                val c = int.toSet() - ext.toSet()
                result = result.withOuterCol(c.single(), y, x)
            }
        }
        return result
    }
}

fun main() {
    fun List<String>.splitToEngravings(): List<Engraving> = chunked(9)
        .flatMap { rows ->
            val splitRows = rows.take(8).map { it.split(" ") }
            splitRows.first().indices.map { j ->
                Engraving(splitRows.map { it[j] })
            }
        }

    fun String.runicPower(): Int = when {
        all { it.isValid } -> mapIndexed { i, c -> (c - 'A' + 1) * (i + 1) }.sum()
        else -> 0
    }

    fun part1(): String = quester.read(1)
        .run(::Engraving)
        .findRunicWord()

    fun part2(): Int = quester.read(2)
        .splitToEngravings()
        .sumOf { it.findRunicWord().runicPower() }

    fun part3(): Int {
        val wall = quester.read(3).map { row -> row.toMutableList() }

        fun List<MutableList<Char>>.engravingAt(baseY: Int, baseX: Int): Engraving {
            val rows = drop(baseY).take(8)
            val shortenedRows = rows.map { it.drop(baseX).take(8).joinToString("") }
            return Engraving(shortenedRows)
        }

        fun List<MutableList<Char>>.overlay(engraving: Engraving, y: Int, x: Int) {
            (0..<8).forEach { dy ->
                (0..<8).forEach { dx ->
                    this[y + dy][x + dx] = engraving.symbols[dy][dx]
                }
            }
        }

        fun List<MutableList<Char>>.solveIncrementally(): Int {
            var totalPower = 0
            for (baseY in 0..lastIndex - 4 step 6) {
                for (baseX in 0..first().lastIndex - 4 step 6) {
                    val engraving = engravingAt(baseY, baseX)
                    val result = engraving.solveUnknowns()
                    totalPower += result.currentRunicWord.runicPower()
                    overlay(result, baseY, baseX)
                }
            }
            return totalPower
        }

        // first pass
        wall.solveIncrementally()
        // second (final) pass
        return wall.solveIncrementally()
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
