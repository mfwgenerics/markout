package io.koalaql.markout.md

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
fun interface MarkdownCheckList {
    @MarkoutDsl
    fun li(checked: Boolean, block: Markdown.() -> Unit)
    @MarkoutDsl
    fun li(checked: Boolean, text: String) = li(checked) { t(text) }
}