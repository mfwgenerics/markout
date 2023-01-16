package io.koalaql.markout.md

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
interface MarkdownInline {
    operator fun Unit.plus(text: String) = +text
    operator fun Unit.plus(unit: Unit) = Unit

    @MarkoutDsl
    fun cite(href: String, title: String? = null): Citation

    @MarkoutDsl
    fun footnote(note: Markdown.() -> Unit)
    @MarkoutDsl
    fun footnote(note: String) = footnote { t(note) }

    @MarkoutDsl
    fun t(text: String)

    operator fun String.unaryPlus() = t(this)

    @MarkoutDsl
    fun c(text: String)

    @MarkoutDsl
    fun a(href: String, line: MarkdownInline.() -> Unit)
    @MarkoutDsl
    fun a(href: Citation, line: MarkdownInline.() -> Unit)

    @MarkoutDsl
    fun a(href: String, text: String) = a(href) { t(text) }
    @MarkoutDsl
    fun a(href: Citation, text: String) = a(href) { t(text) }

    @MarkoutDsl
    fun img(href: String, title: String = "", alt: String = "")

    @MarkoutDsl
    fun t(line: MarkdownInline.() -> Unit)

    @MarkoutDsl
    fun i(block: MarkdownInline.() -> Unit)
    @MarkoutDsl
    fun b(block: MarkdownInline.() -> Unit)
    @MarkoutDsl
    fun s(block: MarkdownInline.() -> Unit)

    @MarkoutDsl
    fun i(text: String) = i { t(text) }
    @MarkoutDsl
    fun b(text: String) = b { t(text) }
    @MarkoutDsl
    fun s(text: String) = s { t(text) }
}