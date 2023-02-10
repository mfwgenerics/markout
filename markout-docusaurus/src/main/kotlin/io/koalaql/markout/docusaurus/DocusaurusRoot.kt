package io.koalaql.markout.docusaurus

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
interface DocusaurusRoot {
    @MarkoutDsl
    fun bootstrap(block: DocusaurusSettings.() -> Unit)
    @MarkoutDsl
    fun docs(block: Docusaurus.() -> Unit)
}