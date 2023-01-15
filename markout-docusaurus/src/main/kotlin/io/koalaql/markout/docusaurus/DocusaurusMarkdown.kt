package io.koalaql.markout.docusaurus

import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.md.Markdown
import io.koalaql.markout.md.markdownString

@MarkoutDsl
interface DocusaurusMarkdown: Markdown {
    @MarkoutDsl
    fun callout(type: String, title: String = "", block: DocusaurusMarkdown.() -> Unit) {
        val contents = markdownString {
            DocusaurusMarkdownImpl(this).block()
        }

        var delimiter = ":::"
        while (contents.contains(delimiter)) delimiter = "$delimiter:"

        raw("$delimiter$type${if (title.isNotBlank()) " $title" else ""}")
        raw(contents)
        raw(delimiter)
    }

    @MarkoutDsl
    fun note(title: String = "", block: DocusaurusMarkdown.() -> Unit) =
        callout("note", title, block)

    @MarkoutDsl
    fun tip(type: String, title: String = "", block: DocusaurusMarkdown.() -> Unit) =
        callout("tip", title, block)

    @MarkoutDsl
    fun info(type: String, title: String = "", block: DocusaurusMarkdown.() -> Unit) =
        callout("info", title, block)

    @MarkoutDsl
    fun caution(type: String, title: String = "", block: DocusaurusMarkdown.() -> Unit) =
        callout("caution", title, block)

    @MarkoutDsl
    fun danger(type: String, title: String = "", block: DocusaurusMarkdown.() -> Unit) =
        callout("danger", title, block)
}