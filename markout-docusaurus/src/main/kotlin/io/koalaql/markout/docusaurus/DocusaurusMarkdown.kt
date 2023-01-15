package io.koalaql.markout.docusaurus

import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.md.Markdown
import io.koalaql.markout.md.MarkdownInline

@MarkoutDsl
interface DocusaurusMarkdown: Markdown {
    @MarkoutDsl
    fun callout(type: String, title: String = "", block: MarkdownInline.() -> Unit) {
        raw(":::$type${if (title.isNotBlank()) " $title" else ""}")
        block()
        raw(":::")
    }
}