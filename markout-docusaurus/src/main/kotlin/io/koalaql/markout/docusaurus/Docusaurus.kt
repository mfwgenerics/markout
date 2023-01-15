package io.koalaql.markout.docusaurus

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
interface Docusaurus {
    @MarkoutDsl
    fun directory(name: String, builder: DocusaurusDirectory.() -> Unit)
    @MarkoutDsl
    fun file(name: String, contents: String)
    @MarkoutDsl
    fun markdown(name: String, builder: DocusaurusMarkdown.() -> Unit)
}