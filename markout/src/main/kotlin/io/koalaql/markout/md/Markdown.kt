package io.koalaql.markout.md

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
interface Markdown: MarkdownInline {
    @MarkoutDsl
    fun h(level: Int, block: MarkdownInline.() -> Unit)

    @MarkoutDsl
    fun h1(block: MarkdownInline.() -> Unit) = h(1, block)
    @MarkoutDsl
    fun h2(block: MarkdownInline.() -> Unit) = h(2, block)
    @MarkoutDsl
    fun h3(block: MarkdownInline.() -> Unit) = h(3, block)
    @MarkoutDsl
    fun h4(block: MarkdownInline.() -> Unit) = h(4, block)
    @MarkoutDsl
    fun h5(block: MarkdownInline.() -> Unit) = h(5, block)
    @MarkoutDsl
    fun h6(block: MarkdownInline.() -> Unit) = h(6, block)

    @MarkoutDsl
    fun h1(text: String) = h1 { t(text) }
    @MarkoutDsl
    fun h2(text: String) = h2 { t(text) }
    @MarkoutDsl
    fun h3(text: String) = h3 { t(text) }
    @MarkoutDsl
    fun h4(text: String) = h4 { t(text) }
    @MarkoutDsl
    fun h5(text: String) = h5 { t(text) }
    @MarkoutDsl
    fun h6(text: String) = h6 { t(text) }

    @MarkoutDsl
    fun p(block: MarkdownInline.() -> Unit)
    @MarkoutDsl
    fun p(text: String) = p { t(text) }

    @MarkoutDsl
    fun hr()

    @MarkoutDsl
    fun quote(block: Markdown.() -> Unit)

    @MarkoutDsl
    fun quote(text: String) = quote { t(text) }

    @MarkoutDsl
    fun code(lang: String, code: String)
    @MarkoutDsl
    fun code(code: String) = code("", code)

    @MarkoutDsl
    fun ol(builder: MarkdownNumberedList.() -> Unit)
    @MarkoutDsl
    fun ul(builder: MarkdownDottedList.() -> Unit)
    @MarkoutDsl
    fun cl(builder: MarkdownCheckList.() -> Unit)

    @MarkoutDsl
    fun table(builder: MarkdownTable.() -> Unit)
}