package io.koalaql.markout.docusaurus

import io.koalaql.markout.md.Markdown

class MarkdownFileImpl(
    private val markdown: Markdown,
    private val position: Int,
): DocusaurusMarkdownFile, Markdown by markdown {
    override var slug: String = ""

    fun header(): String {
        val sb = StringBuilder()

        sb.append("---\n")
        sb.append("sidebar_position: $position\n")
        if (slug.isNotBlank()) {
            sb.append("slug: $slug\n")
        }
        sb.append("---\n")

        return "$sb"
    }
}