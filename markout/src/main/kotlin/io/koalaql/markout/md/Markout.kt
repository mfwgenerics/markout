package io.koalaql.markout.md

import io.koalaql.markout.MarkdownBuilder
import io.koalaql.markout.Markout
import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.text.AppendableLineWriter

fun markdownString(builder: Markdown.() -> Unit): String {
    val sb = StringBuilder()
    val cites = linkedMapOf<String, Citation>()

    MarkdownBuilder(AppendableLineWriter(sb), cites).builder()

    if (cites.isNotEmpty()) {
        sb.append("\n")
        cites.forEach { (key, cite) ->
            sb.append("\n")
            sb.append(cite.label)
            sb.append(": ")
            sb.append(key)
        }
    }

    return "$sb"
}

@MarkoutDsl
fun Markout.markdown(name: String, builder: Markdown.() -> Unit) {
    file("$name.md", markdownString(builder))
}