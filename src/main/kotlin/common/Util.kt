package it.pidaydev.common

import java.io.File

/** Reads lines from the given input txt file. */
fun readInput(year: Int, quest: Int, part: Int) = File(
    "src/main/resources/inputs",
    "everybody_codes_e${year}_q${quest.toString().padStart(2, '0')}_p${part}.txt"
).readLines()
