package io.koalaql.markout.md

import io.koalaql.kapshot.CapturedBlock
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
    fun <T> code(lang: String, code: CapturedBlock<T>): T {
        code(lang, code.source())

        return code()
    }

    @MarkoutDsl
    fun <T> code(code: CapturedBlock<T>): T = code("", code)

    @MarkoutDsl
    fun ol(builder: MarkdownNumberedList.() -> Unit)
    @MarkoutDsl
    fun ul(builder: MarkdownDottedList.() -> Unit)
    @MarkoutDsl
    fun cl(builder: MarkdownCheckList.() -> Unit)
}