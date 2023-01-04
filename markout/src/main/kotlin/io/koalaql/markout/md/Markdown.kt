package io.koalaql.markout.md

interface Markdown: MarkdownBlock {
    fun h1(block: MarkdownInline.() -> Unit)
    fun h2(block: MarkdownInline.() -> Unit)
    fun h3(block: MarkdownInline.() -> Unit)

    fun h1(text: String) = h1 { t(text) }
    fun h2(text: String) = h2 { t(text) }
    fun h3(text: String) = h3 { t(text) }

    fun p(block: MarkdownBlock.() -> Unit)
}