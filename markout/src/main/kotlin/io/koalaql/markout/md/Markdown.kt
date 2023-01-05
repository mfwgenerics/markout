package io.koalaql.markout.md

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
interface Markdown: MarkdownInline {
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
    fun p(block: MarkdownInline.() -> Unit)
    @MarkoutDsl
    fun p(text: String) = p { t(text) }

    @MarkoutDsl
    fun quote(block: Markdown.() -> Unit)

    @MarkoutDsl
    fun quote(text: String) = quote { t(text) }

    @MarkoutDsl
    fun code(code: String)

    @MarkoutDsl
    fun ol(builder: MarkdownNumberedList.() -> Unit)
    @MarkoutDsl
    fun ul(builder: MarkdownDottedList.() -> Unit)
}