package io.koalaql.markout.md

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
interface Markdown: MarkdownBlock {
    @MarkoutDsl
    fun h1(block: MarkdownInline.() -> Unit)
    @MarkoutDsl
    fun h2(block: MarkdownInline.() -> Unit)
    @MarkoutDsl
    fun h3(block: MarkdownInline.() -> Unit)

    @MarkoutDsl
    fun h1(text: String) = h1 { t(text) }
    @MarkoutDsl
    fun h2(text: String) = h2 { t(text) }
    @MarkoutDsl
    fun h3(text: String) = h3 { t(text) }

    @MarkoutDsl
    fun p(block: MarkdownBlock.() -> Unit)
    @MarkoutDsl
    fun p(text: String) = p { t(text) }
}