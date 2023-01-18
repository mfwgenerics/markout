package io.koalaql.markout.md

import io.koalaql.markout.MarkdownBuilder
import io.koalaql.markout.Markout
import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.text.AppendableLineWriter

fun markdownString(
    trailingNewline: Boolean = false,
    builder: Markdown.() -> Unit
): String {
    val sb = StringBuilder()

    MarkdownBuilder(AppendableLineWriter(sb), top = true).apply {
        builder()
        footer()
    }

    if (sb.isEmpty()) return ""

    if (trailingNewline) sb.append("\n")

    return "$sb"
}

@MarkoutDsl
fun Markout.markdown(name: String, contents: String) {
    val suffixed = when {
        name.endsWith(".md", ignoreCase = true) ||
                name.endsWith(".mdx", ignoreCase = true) -> name
        else -> "$name.md"
    }

    file(suffixed, contents)
}

@MarkoutDsl
fun Markout.markdown(name: String, builder: Markdown.() -> Unit) {
    markdown(name, markdownString(trailingNewline = true, builder))
}