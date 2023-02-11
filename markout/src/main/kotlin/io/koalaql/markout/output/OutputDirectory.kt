package io.koalaql.markout.output

fun interface OutputDirectory: Output {
    fun entries(): Map<String, OutputEntry>
}