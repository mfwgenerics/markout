package io.koalaql.markout.docusaurus

import java.io.InputStream

object Resources {
    fun open(path: String): InputStream =
        checkNotNull(javaClass.getResource(path)) { "no resource found at $path" }.openStream()
}