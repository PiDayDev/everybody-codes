package it.pidaydev.y24

import it.pidaydev.common.quest

private val quester = YEAR quest 7 withRowParser { it.toPlan() }

private fun String.toPlan(): ActionPlan {
    val (name, actions) = split(":")
    return ActionPlan(name, actions.split(",").map { Action.fromString(it) })
}

private enum class Action(val char: Char) {
    INC('+') {
        override fun apply(k: Long) = k + 1
    },
    DEC('-') {
        override fun apply(k: Long) = k - 1
    },
    SAME('=') {
        override fun apply(k: Long) = k
    };

    abstract fun apply(k: Long): Long

    override fun toString() = "$char"

    companion object {
        fun fromString(s: String) =
            entries.firstOrNull { "${it.char}" == s } ?: SAME

        fun fromChar(c: Char) =
            entries.firstOrNull { it.char == c } ?: SAME
    }
}

private data class ActionPlan(val name: String, val actions: List<Action>) {
    override fun toString() = "Plan($name: ${actions.joinToString("")})"

    fun score(
        segments: Int,
        override: List<Action> = listOf(Action.SAME)
    ): Long =
        (0 until segments)
            .runningFold(10L) { power, segment ->
                val action = override
                    .getOrNull(segment % override.size)
                    .takeIf { it != Action.SAME }
                    ?: actions[segment % actions.size]
                action.apply(power)
            }
            .drop(1)
            .sum()
}

fun main() {

    fun part1(): String {
        val actionPlans = quester.read(1)
        val results = actionPlans.associateWith { plan ->
            plan.score(10)
        }
        val sorted = results.toList().sortedByDescending { (_, essence) -> essence }
        return sorted.joinToString("") { (plan, _) -> plan.name }
    }

    fun part2(): String {
        val actionPlans = quester.read(2)
        val track = trackMap2.linearizeTrack()

        val segments = track.count() * 10

        val results = actionPlans.associateWith { plan ->
            plan.score(segments, track)
        }
        val sorted = results.toList().sortedByDescending { (_, essence) -> essence }
        return sorted.joinToString("") { (plan, _) -> plan.name }
    }

    fun part3(): Int {
        val enemyPlan = quester.read(3).first()
        val actionsPerPlan = enemyPlan.actions.size

        val track = trackMap3.linearizeTrack()

        // skip useless loops
        val repetitions = actionsPerPlan + 2024 % actionsPerPlan
        val segments = track.count() * repetitions

        val enemyScore = enemyPlan.score(segments, track)

        val winningPlansCount = anagrams("+++++---===")
            .map { plan -> ActionPlan(plan, plan.map { Action.fromChar(it) }) }
            .map { it.score(segments, track) }
            .count { score -> score > enemyScore }

        return winningPlansCount
    }

    quester.printAndVerify(::part1, ::part2, ::part3)

}

fun anagrams(s: String): Sequence<String> {
    if (s.length == 1) return sequenceOf(s)
    val chars = s.asSequence()
    return chars.distinct().flatMap { c ->
        anagrams((chars - c).joinToString("")).map { it + c }
    }
}

val trackMap2 = """
    S-=++=-==++=++=-=+=-=+=+=--=-=++=-==++=-+=-=+=-=+=+=++=-+==++=++=-=-=--
    -                                                                     -
    =                                                                     =
    +                                                                     +
    =                                                                     +
    +                                                                     =
    =                                                                     =
    -                                                                     -
    --==++++==+=+++-=+=-=+=-+-=+-=+-=+=-=+=--=+++=++=+++==++==--=+=++==+++-
""".trimIndent().lines()

val trackMap3 = """
    S+= +=-== +=++=     =+=+=--=    =-= ++=     +=-  =+=++=-+==+ =++=-=-=--
    - + +   + =   =     =      =   == = - -     - =  =         =-=        -
    = + + +-- =-= ==-==-= --++ +  == == = +     - =  =    ==++=    =++=-=++
    + + + =     +         =  + + == == ++ =     = =  ==   =   = =++=
    = = + + +== +==     =++ == =+=  =  +  +==-=++ =   =++ --= + =
    + ==- = + =   = =+= =   =       ++--          +     =   = = =--= ==++==
    =     ==- ==+-- = = = ++= +=--      ==+ ==--= +--+=-= ==- ==   =+=    =
    -               = = = =   +  +  ==+ = = +   =        ++    =          -
    -               = + + =   +  -  = + = = +   =        +     =          -
    --==++++==+=+++-= =-= =-+-=  =+-= =-= =--   +=++=+++==     -=+=++==+++-
""".trimIndent().lines()

private fun List<String>.linearizeTrack(): List<Action> {
    val visited = mutableSetOf(0 to 0)
    val segments = mutableListOf('S')
    var x = 1
    var y = 0
    while ((x to y) !in visited) {
        visited += x to y
        segments += this[y][x]
        val next = listOf(x + 1 to y, x - 1 to y, x to y + 1, x to y - 1)
            .filter { it !in visited }
            .firstOrNull { (xc, yc) -> (getOrNull(yc)?.getOrNull(xc) ?: ' ') != ' ' }
            ?: break
        x = next.first
        y = next.second
    }
    // move initial S to end
    return (segments.drop(1) + segments.take(1))
        .map { Action.fromChar(it) }
}
