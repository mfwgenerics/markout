package io.koalaql.markout.md

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
interface MarkdownInline {
    @MarkoutDsl
    fun cite(href: String, title: String? = null): Citation

    @MarkoutDsl
    fun t(text: String)

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