package io.koalaql.markout.md

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
fun interface MarkdownNumberedList {
    @MarkoutDsl
    fun li(number: Int?, block: Markdown.() -> Unit)
    @MarkoutDsl
    fun li(block: Markdown.() -> Unit) = li(null, block)

    @MarkoutDsl
    fun li(number: Int, text: String) = li(number) { t(text) }
    @MarkoutDsl
    fun li(text: String) = li { t(text) }
}