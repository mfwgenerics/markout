package docusaurus

import io.koalaql.markout.Markout
import io.koalaql.markout.docusaurus.docusaurus

fun Markout.setupDocusaurus() = docusaurus {
    configure {
        url = "https://mfwgenerics.github.io/"
        baseUrl = "/markout/"

        title = "Markout"

        github = "https://github.com/mfwgenerics/markout"

        metadata = mapOf("google-site-verification" to "E-XuQoF0UqA8bzoXL3yY7bs9KuQFsQ2yrSkYuIp6Gqs")
    }

    docs {
        intro()
    }
}