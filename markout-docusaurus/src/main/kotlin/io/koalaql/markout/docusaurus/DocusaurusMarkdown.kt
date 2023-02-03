package io.koalaql.markout.docusaurus

import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.md.Markdown
import io.koalaql.markout.md.markdown

@MarkoutDsl
interface DocusaurusMarkdown: Markdown {
    @MarkoutDsl
    fun code(lang: String, title: String, code: String) =
        code("$lang title=\"$title\"", code)

    @MarkoutDsl
    fun code(lang: String, title: String, highlight: ClosedRange<Int>, code: String) {
        val lines = if (highlight.start == highlight.endInclusive) {
            "{${highlight.start}}"
        } else {
            "{${highlight.start}-${highlight.endInclusive}}"
        }

        code("$lang title=\"$title\" $lines", code)
    }

    @MarkoutDsl
    fun code(lang: String, title: String, highlight: Int, code: String) =
        code(lang, title, highlight..highlight, code)

    @MarkoutDsl
    fun callout(type: String, title: String = "", block: DocusaurusMarkdown.() -> Unit) {
        val contents = markdown {
            DocusaurusMarkdownWrapper(this).block()
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
    fun tip(title: String = "", block: DocusaurusMarkdown.() -> Unit) =
        callout("tip", title, block)

    @MarkoutDsl
    fun info(title: String = "", block: DocusaurusMarkdown.() -> Unit) =
        callout("info", title, block)

    @MarkoutDsl
    fun caution(title: String = "", block: DocusaurusMarkdown.() -> Unit) =
        callout("caution", title, block)

    @MarkoutDsl
    fun danger(title: String = "", block: DocusaurusMarkdown.() -> Unit) =
        callout("danger", title, block)
}