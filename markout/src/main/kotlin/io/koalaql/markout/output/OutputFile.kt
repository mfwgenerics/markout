package io.koalaql.markout.output

import java.io.OutputStream

fun interface OutputFile: Output {
    fun writeTo(output: OutputStream)
}