package io.koalaql.markout.md

interface MarkdownInline {
    fun t(text: String)

    fun t(line: MarkdownInline.() -> Unit)

    fun i(block: Markdown.() -> Unit)
    fun b(block: Markdown.() -> Unit)

    fun i(text: String) = i { t(text) }
    fun b(text: String) = b { t(text) }

    fun code(code: String)
}