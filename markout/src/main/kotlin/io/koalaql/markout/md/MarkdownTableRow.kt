package io.koalaql.markout.md

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
fun interface MarkdownTableRow {
    @MarkoutDsl
    fun td(cell: MarkdownInline.() -> Unit)
    @MarkoutDsl
    fun td(text: String) = td { t(text) }
}