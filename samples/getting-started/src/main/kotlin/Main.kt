package myproject

import io.koalaql.markout.markout
import io.koalaql.markout.md.markdown

fun main() = markout {
    markdown("hello") {
        h1("Hello world")
    }
}