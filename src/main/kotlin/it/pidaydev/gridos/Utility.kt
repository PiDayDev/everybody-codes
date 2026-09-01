package it.pidaydev.gridos

private val whitespace = """\s+""".toRegex()

fun cartesianCombo(a: List<String>, b: List<String>) {
    fun instructions(strings: List<String>) = strings
        .dropWhile { it.startsWith("HEADS") }
        .map { it.split(whitespace) }

    val list1 = instructions(a)
    val list2 = instructions(b)

    for (x in list1) {
        for (y in list2) {
            val combo = x.zip(y) { m, n ->
                if (m == n && (n == "START" || n == "STOP")) m else m + n
            }
            println(combo.joinToString(" "))
        }
    }
}

fun cartesianCombo(a: String, b: String) =
    cartesianCombo(
        a.lines().filter { it.isNotBlank() },
        b.lines().filter { it.isNotBlank() },
    )

fun main() {
    val a = """
        CLEAN X STOP  _ S
        CLEAN 1 ERASE | R
        BKSPC X STOP  _ S
        ERASE ! ERASE _ R
        ERASE _ NEXXT * D
        NEXXT _ BKSPC * L
        NEXXT ! CANNC _ R
        CANNC ! CANNC _ R
        CANNC _ BKSPC _ L
        BKSPC _ BKSPC _ L
        BKSPC = BKSPC _ L
        BKSPC | BKSPC _ L
        BKSPC 1 CLEAN | D
    """.trimIndent()
    val b = """
        8LEAN X STOP  _ S
        8LEAN 1 8RASE | R
        8KSPC X STOP  _ S
        8RASE ! 8RASE _ R
        8RASE _ 8EXXT * U
        8EXXT _ 8KSPC * L
        8EXXT ! 8ANNC _ R
        8ANNC ! 8ANNC _ R
        8ANNC _ 8KSPC _ L
        8KSPC _ 8KSPC _ L
        8KSPC = 8KSPC _ L
        8KSPC | 8KSPC _ L
        8KSPC 1 8LEAN | U
    """.trimIndent()
    cartesianCombo(a, b)
}
