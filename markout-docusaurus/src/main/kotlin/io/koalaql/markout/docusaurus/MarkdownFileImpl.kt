package io.koalaql.markout.docusaurus

import io.koalaql.markout.md.Markdown

class MarkdownFileImpl(
    private val markdown: Markdown
): DocusaurusMarkdownFile, Markdown by markdown {
    override var slug: String = ""

    fun sidebar(position: Int) {
        raw(
            """
            ---
            sidebar_position: $position
            ---
            """.trimIndent()
        )
    }
}