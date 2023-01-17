package io.koalaql.markout.md

import io.koalaql.markout.MarkdownBuilder
import io.koalaql.markout.Markout
import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.text.AppendableLineWriter

fun markdownString(builder: Markdown.() -> Unit): String {
    val sb = StringBuilder()

    MarkdownBuilder(AppendableLineWriter(sb), top = true).apply {
        builder()
        footer()
    }

    return "$sb"
}

@MarkoutDsl
fun Markout.markdown(name: String, builder: Markdown.() -> Unit) {
    val suffixed = when {
        name.endsWith(".md", ignoreCase = true) ||
        name.endsWith(".mdx", ignoreCase = true) -> name
        else -> "$name.md"
    }

    file(suffixed, markdownString(builder))
}