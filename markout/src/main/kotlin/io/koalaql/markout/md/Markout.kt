package io.koalaql.markout.md

import io.koalaql.markout.MarkdownBuilder
import io.koalaql.markout.Markout
import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.text.AppendableLineWriter

fun markdownString(builder: Markdown.() -> Unit): String {
    val sb = StringBuilder()

    MarkdownBuilder(AppendableLineWriter(sb)).builder()

    return "$sb"
}

@MarkoutDsl
fun Markout.markdown(name: String, builder: Markdown.() -> Unit) {
    file("$name.md", markdownString(builder))
}