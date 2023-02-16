package docusaurus

import drawFileTree
import io.koalaql.kapshot.CapturedBlock
import io.koalaql.markout.Markout
import io.koalaql.markout.buildOutput
import io.koalaql.markout.docusaurus.docusaurus
import io.koalaql.markout.md.markdown
import io.koalaql.markout.output.OutputDirectory
import io.koalaql.markout.output.OutputEntry

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
                -"to "+a("https://github.com/Kotlin/kotlinx-knit", "kotlinx-knit")+"."
            }

            h2("Project purpose")

            p {
                -"Documenting code is a time-consuming and error-prone process."
                -"Not only is handwritten sample code vulnerable to typos and syntax errors,"
                -"it silently goes out of date as projects evolve."
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

            h2("How it works")

            -"Markout generates files by running code from Kotlin projects with the Markout Gradle plugin applied."
            -"You supply a `main` method which invokes a `markout` block to describe how files should be generated."
            -"This code runs every time files are generated or verified."

            var fileTree: String = ""

            fun markout(builder: Markout.() -> Unit) {
                fileTree = drawFileTree(object : OutputDirectory {
                    override fun entries(): Map<String, OutputEntry> = mapOf("my-project" to OutputEntry(
                        tracked = false,
                        buildOutput(builder)
                    ))
                }, dotfiles = false)
            }

            fun execBlock(block: CapturedBlock<Unit>): String =
                block.source.text.also { block.invoke() }

            code("kotlin", "Main.kt", "fun main() = ${execBlock {
                markout {
                    file("README.txt", "Hello world!")

                    directory("docs") {
                        file("INTRO.txt", "Another text file!")
                        file("OUTRO.txt", "A final text file")
                    }
                }
            }}")

            -"When the code above is run using `:markout`, it generates the following files"
            -"and creates them into the project directory."

            code(fileTree)

            -"The `:markoutCheck` task then verifies that these files match subsequent runs of the code."
        }
    }
}