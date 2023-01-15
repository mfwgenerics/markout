package io.koalaql.markout.docusaurus

import io.koalaql.markout.Markout
import io.koalaql.markout.MarkoutDsl

@MarkoutDsl
fun Markout.docusaurus(block: DocusaurusContext.() -> Unit) {
    object : DocusaurusContext {
        override fun directory(name: String, builder: Markout.() -> Unit) {
            this@docusaurus.directory(name, builder)
        }

        override fun file(name: String, contents: String) {
            this@docusaurus.file(name, contents)
        }
    }.block()
}