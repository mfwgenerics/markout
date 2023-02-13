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
        markdown("intro") {
            slug = "/"

            h1("Introduction")

            p {
                -"Markout is an executable documentation platform for Kotlin that"
                -"allows you to document Kotlin projects in code rather than text."
                -"Documentation written this way can be tested and verified on every build."
                -"Sample code becomes error proof, stays up-to-date and forms an"
                -"extra test suite for your project. Markout can serve as an alternative"
                -"to "+a("https://github.com/Kotlin/kotlinx-knit", "kotlinx-knit")
            }

            h2("Project Purpose")

            p {
                -"Documenting code is a time-consuming and error-prone process."
                -"Handwritten sample code is vulnerable to typos and syntax errors"
                -"and it silently goes out of date as projects evolve."
                -"Results shown in documentation are also not guaranteed to match the"
                -"real behavior of the code. Markout seeks to address this by allowing"
                -"your docs to execute code from your project and embed the results."
                -"Your generated documentation is checked into Git and used to perform"
                -"snapshot testing on future builds."
            }

            p {
                -"Another goal of this project is to make it easy for Kotlin developers to use the"
                -" "+a("https://docusaurus.io/", "Docusaurus")+" static site generator to quickly build"
                -"and deploy documentation on GitHub pages. Markout can create, configure, install, build"
                -"and run Docusaurus projects without requiring Node.js to be installed. It integrates"
                -"with Gradle's "
                a(
                    "https://docs.gradle.org/current/userguide/command_line_interface.html#sec:continuous_build",
                    "Continuous Build"
                )
                -"to enable hot reloads and previews as you code."
                -"Docusaurus support is optional and provided through a separate Gradle plugin."
            }

            p {
                -"Markout is designed to integrate with "+a("https://github.com/mfwgenerics/kapshot", "Kapshot")+","
                -"a minimal Kotlin compiler plugin that allows source code to be"
                -"captured and inspected at runtime."
                -"Kapshot is the magic ingredient that enables fully executable and testable sample code blocks."
            }
        }
    }
}