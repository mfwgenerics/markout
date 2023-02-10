package docusaurus

import io.koalaql.markout.Markout
import io.koalaql.markout.docusaurus.docusaurus

fun Markout.setupDocusaurus() = docusaurus {
    configure()

    docs {
        markdown("hello") {
            h1("Hello")
        }
    }
}