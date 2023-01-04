package io.koalaql.markout.md

interface MarkdownBlock: MarkdownInline {
    fun quote(block: Markdown.() -> Unit)

    fun quote(text: String) = quote { t(text) }

    fun ol(builder: MarkdownList.() -> Unit)
    fun ul(builder: MarkdownList.() -> Unit)
}