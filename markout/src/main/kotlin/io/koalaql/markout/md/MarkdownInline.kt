package io.koalaql.markout.md

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
interface MarkdownInline {
    @MarkoutDsl
    fun t(text: String)

    @MarkoutDsl
    fun c(text: String)

    @MarkoutDsl
    fun t(line: MarkdownInline.() -> Unit)

    @MarkoutDsl
    fun i(block: MarkdownInline.() -> Unit)
    @MarkoutDsl
    fun b(block: MarkdownInline.() -> Unit)

    @MarkoutDsl
    fun i(text: String) = i { t(text) }
    @MarkoutDsl
    fun b(text: String) = b { t(text) }

    @MarkoutDsl
    fun code(code: String)
}