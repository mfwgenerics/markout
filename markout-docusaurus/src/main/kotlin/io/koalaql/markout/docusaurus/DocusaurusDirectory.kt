package io.koalaql.markout.docusaurus

import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
interface DocusaurusDirectory: Docusaurus {
    @MarkoutDsl
    var label: String

    @MarkoutDsl
    fun link(
        description: String = ""
    )
}