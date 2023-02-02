package io.koalaql.markout.md

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
interface MarkdownTable {
    @MarkoutDsl
    fun th(row: MarkdownTableRow.() -> Unit)
    @MarkoutDsl
    fun tr(row: MarkdownTableRow.() -> Unit)
}