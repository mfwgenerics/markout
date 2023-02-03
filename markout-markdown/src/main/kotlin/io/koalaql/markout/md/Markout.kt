package io.koalaql.markout.md

import io.koalaql.markout.MarkdownBuilder
import io.koalaql.markout.Markout
import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.text.AppendableLineWriter

fun markdown(
    builder: Markdown.() -> Unit
): String {
    val sb = StringBuilder()

    MarkdownBuilder(AppendableLineWriter(sb), top = true).apply {
        builder()
        footer()
    }

    return "$sb"
}

private fun withSuffix(name: String) = when {
    name.endsWith(".md", ignoreCase = true) ||
    name.endsWith(".mdx", ignoreCase = true) -> name
    else -> "$name.md"
}

@MarkoutDsl
fun Markout.markdown(name: String, contents: String) {
    file(withSuffix(name), contents)
}

@MarkoutDsl
fun Markout.markdown(name: String, builder: Markdown.() -> Unit) {
    file(withSuffix(name)) { out ->
        val writer = out.writer()

        val lw = AppendableLineWriter(writer)

        MarkdownBuilder(lw, top = true).apply {
            builder()
            footer()
        }

        lw.newline()

        writer.close()
    }
}