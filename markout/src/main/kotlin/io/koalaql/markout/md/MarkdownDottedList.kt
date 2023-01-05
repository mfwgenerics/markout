package io.koalaql.markout.md

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
fun interface MarkdownDottedList {
    @MarkoutDsl
    fun li(block: Markdown.() -> Unit)

    @MarkoutDsl
    fun li(text: String) = li { t(text) }
}