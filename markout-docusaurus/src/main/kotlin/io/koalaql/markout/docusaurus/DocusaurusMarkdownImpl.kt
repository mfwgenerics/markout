package io.koalaql.markout.docusaurus

import io.koalaql.markout.md.Markdown

class DocusaurusMarkdownImpl(
    private val markdown: Markdown
): DocusaurusMarkdown, Markdown by markdown {
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