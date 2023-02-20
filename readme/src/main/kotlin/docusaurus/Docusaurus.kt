package docusaurus

import io.koalaql.markout.Markout
import io.koalaql.markout.docusaurus.docusaurus
import MARKOUT_VERSION
import docusaurus.start.projectStructure
import io.koalaql.markout.md.MarkdownTable

fun MarkdownTable.pluginRow(id: String, function: String) {
    tr {
        td { c(id) }
        td(function)
        td {
            a("https://plugins.gradle.org/plugin/$id", "Gradle")
        }
    }
}

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

        directory("getting-started") {
            label = "Getting started"

            projectStructure()

            markdown("markdown") {
                h1("Generating markdown")
            }
        }
    }
}