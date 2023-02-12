package docusaurus

import io.koalaql.markout.Markout
import io.koalaql.markout.docusaurus.docusaurus

fun Markout.setupDocusaurus() = docusaurus {
    configure {
        url = "https://localhost:3000"
    }

    docs {
        markdown("hello") {
            slug = "/"

            h1("Hello")
        }
    }
}