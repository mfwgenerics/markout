package io.koalaql.markout.md

import io.koalaql.markout.MarkdownBuilder
import io.koalaql.markout.Markout
import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.text.AppendableLineWriter
import io.koalaql.markout.text.LineWriter

fun markdownTo(
    writer: LineWriter,
    builder: Markdown.() -> Unit
) {
    MarkdownBuilder(writer, top = true).apply {
        builder()
        footer()
    }
}

fun markdown(
    builder: Markdown.() -> Unit
): String {
    val sb = StringBuilder()

    markdownTo(AppendableLineWriter(sb), builder)

    return "$sb"
}

fun withMdSuffix(name: String) = when {
    name.endsWith(".md", ignoreCase = true) ||
    name.endsWith(".mdx", ignoreCase = true) -> name
    else -> "$name.md"
}

@MarkoutDsl
fun Markout.markdown(name: String, contents: String) {
    file(withMdSuffix(name), contents)
}

@MarkoutDsl
fun Markout.markdown(name: String, builder: Markdown.() -> Unit) {
    file(withMdSuffix(name)) { out ->
        val writer = out.writer()

        val lw = AppendableLineWriter(writer)

        markdownTo(lw, builder)

        lw.newline()

        writer.flush()
    }
}