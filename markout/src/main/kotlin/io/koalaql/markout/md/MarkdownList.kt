package io.koalaql.markout.md

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
interface MarkdownList {
    @MarkoutDsl
    fun item(block: Markdown.() -> Unit)

    @MarkoutDsl
    fun item(text: String) = item { t(text) }
}