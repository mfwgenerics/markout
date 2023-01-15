package io.koalaql.markout.docusaurus

import io.koalaql.markout.Markout
import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.md.Markdown
import io.koalaql.markout.md.markdownString

@MarkoutDsl
interface DocusaurusContext {
    @MarkoutDsl
    fun directory(name: String, builder: Markout.() -> Unit)
    @MarkoutDsl
    fun file(name: String, contents: String)

    @MarkoutDsl
    fun markdown(name: String, builder: Markdown.() -> Unit) {
        file("$name.md", markdownString(builder))
    }
}