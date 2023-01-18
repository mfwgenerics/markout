package io.koalaql.markout.docusaurus

import io.koalaql.markout.text.AppendableLineWriter
import io.koalaql.markout.text.LineWriter

class JsonBuilder(
    private val lines: LineWriter,
    private val indent: String
) {
    private var prefix = ""

    fun write(text: String) {
        lines.raw(text)
    }

    fun item(builder: JsonBuilder.() -> Unit) {
        lines.raw(prefix)
        prefix = ",\n"

        builder()
    }

    private fun writeString(value: String) {
        write("\"${value}\"") // TODO escape
    }

    infix fun String.braces(builder: JsonBuilder.() -> Unit) = item {
        writeString(this@braces)
        write(": ")
        braces(builder)
    }

    operator fun String.minus(value: String) = item {
        writeString(this@minus)
        write(": ")
        writeString(value)
    }

    operator fun String.minus(value: Number) = item {
        writeString(this@minus)
        write(": ")
        write("$value")
    }

    fun indented(builder: JsonBuilder.() -> Unit) {
        JsonBuilder(lines.prefixed(indent), indent).builder()
    }

    fun block(start: String, end: String, builder: JsonBuilder.() -> Unit) {
        lines.inline(start)
        lines.newline()
        indented(builder)
        lines.newline()
        lines.inline(end)
    }

    fun braces(builder: JsonBuilder.() -> Unit) =
        block("{", "}", builder)
}

fun writeJson(
    indent: String,
    builder: JsonBuilder.() -> Unit
): String {
    val sb = StringBuilder()

    JsonBuilder(AppendableLineWriter(sb), indent).builder()

    return "$sb"
}