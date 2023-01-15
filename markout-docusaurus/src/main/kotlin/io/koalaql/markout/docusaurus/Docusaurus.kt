package io.koalaql.markout.docusaurus

import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.md.Markdown

@MarkoutDsl
interface Docusaurus {
    @MarkoutDsl
    fun directory(name: String, builder: DocusaurusDirectory.() -> Unit)
    @MarkoutDsl
    fun file(name: String, contents: String)
    @MarkoutDsl
    fun markdown(name: String, builder: Markdown.() -> Unit)
}