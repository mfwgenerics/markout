package io.koalaql.markout.md

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
interface MarkdownBlock: MarkdownInline {
    @MarkoutDsl
    operator fun String.unaryMinus() {
        t("\n")
        t(this)
    }
}