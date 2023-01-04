package io.koalaql.markout.md

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
interface MarkdownBlock: MarkdownInline {
    @MarkoutDsl
    fun quote(block: Markdown.() -> Unit)

    @MarkoutDsl
    fun quote(text: String) = quote { t(text) }

    @MarkoutDsl
    fun ol(builder: MarkdownList.() -> Unit)
    @MarkoutDsl
    fun ul(builder: MarkdownList.() -> Unit)
}