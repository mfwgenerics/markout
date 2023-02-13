import docusaurus.setupDocusaurus
import io.koalaql.markout.markout
import io.koalaql.markout.md.markdown
import workflows.checkYml
import workflows.deployGhPagesYml
import workflows.releaseYml
import kotlin.io.path.Path

private val CURRENT_VERSION = "0.0.6"

fun main() = markout {
    directory(".github") {
        directory("workflows") {
            checkYml()
            releaseYml()
            deployGhPagesYml()
        }
    }

    directory("docusaurus") {
        setupDocusaurus()
    }

    directory("docs") {
        markdownDocs()
        fileGen()
    }

    markdown("README") {
        h1("Markout: Markdown DSL and File Generator")

        -"Markout is a library for generating files, directories and Markdown documentation from Kotlin."
        -"It is designed for generating GitHub Flavored Markdown docs that live alongside code."
        -"Using "+a("https://github.com/mfwgenerics/kapshot", "Kapshot")+" with this project"
        -"allows literate programming and \"executable documentation\" which ensures"
        -"that documentation remains correct and up to date."

        sectioned {
            section("Intro") {
                p {
                    -"Markout provides a fully featured Markdown DSL to support documentation"
                    -"generation and automation. It is flexible, mixes easily with raw markdown and"
                    -"is intended to be built upon and used in conjunction with other tools."
                    -"The Markdown DSL can build strings or output directly to a file."
                }

                p {
                    -"In addition to the Markdown DSL, Markout provides tools for managing"
                    -"generated files and directories. Files and directories can be declared using"
                    -"a DSL and then validated or synchronized. Snapshot testing can be performed on"
                    -"generated files."
                }
            }

            section("Getting Started") {
                -"Add the `markout` dependency"

                code(
                    "kotlin",
                    """
                    /* build.gradle.kts */
                    dependencies {
                        implementation("io.koalaql:markout:$CURRENT_VERSION")
                    }
                    """.trimIndent()
                )

                h4("File Generation")

                -"If you want to use Markout as a documentation generator, call"
                -"the `markout` function directly from your main method. Pass a path"
                -"to the directory where you want Markout to generate files."
                -"The path can be relative or absolute."

                code {
                    fun main() = markout(Path(".")) {
                        markdown("hello") {
                            p("This file was generated using markout")

                            p {
                                i("Hello ") + "World!"
                            }
                        }
                    }
                }

                -"Currently the Gradle application plugin is the best way to run a standalone Markout project"

                code("shell", "./gradlew :my-project:run")

                h4("Markdown Strings")

                -"If you only want to use Markout to generate Markdown strings then you can use"
                -"`markdown` as a standalone function"

                val result = code {
                    markdown {
                        h1("My Markdown")

                        -"Text with some *italics*."
                    }
                }

                -"The above will produce the String"

                code("markdown", result)
            }

            section("Usage") {
                ol {
                    li { a("docs/FILES.md", "File Generation") }
                    li { a("docs/MARKDOWN.md", "Markdown") }
                }
            }
        }
    }
}