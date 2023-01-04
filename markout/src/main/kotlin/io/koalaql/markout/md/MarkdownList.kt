package io.koalaql.markout.md

interface MarkdownList {
    fun item(block: Markdown.() -> Unit)

    fun item(text: String) = item { t(text) }
}