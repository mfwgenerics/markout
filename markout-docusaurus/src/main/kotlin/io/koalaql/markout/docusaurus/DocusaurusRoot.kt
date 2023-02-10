package io.koalaql.markout.docusaurus

import io.koalaql.markout.MarkoutDsl

interface DocusaurusRoot: Docusaurus {
    @MarkoutDsl
    fun bootstrap()
}