import io.koalaql.markout.markout
import io.koalaql.markout.md.markdown
import io.koalaql.markout.md.markdownString
import kotlin.io.path.Path

private val CURRENT_VERSION = "0.0.3"

fun main() = markout(Path("..")) {
    directory("docs") {
        basic()
        extended()
        fileGen()
    }

    markdown("README") {
        h1("Markout")

        -"Markout is a library for generating files, directories and Markdown documentation from Kotlin."
        -"It is designed for generating GitHub Flavored Markdown docs that live alongside code." +
        -"Using "+a("https://github.com/mfwgenerics/kapshot", "Kapshot")+" with this project"
        -"allows literate programming and \"executable documentation\", enabling developers"
        -"to ensure that documentation remains correct and up to date."

        sectioned {
            section("Getting Started") {
                -"Add the markout dependency"

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

                -"If you want to use markout as a documentation generator, call"
                -"the `markout` function directly from your main method. Pass a path"
                -"to the directory where you want markout to generate files."
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

                -"Currently the Gradle application plugin is the best way to run a standalone markout project"

                code("shell", "./gradlew :my-project:run")

                h4("Markdown Strings")

                -"If you only want to use markout to generate Markdown strings then you can use"
                -"`${::markdownString.name}`"

                val result = code {
                    markdownString {
                        h1("My Markdown")

                        -"Text with some "+i("italics")+"."
                    }
                }

                -"The above will produce the String"

                code("markdown", result)
            }

            section("Usage") {
                ol {
                    li { a("docs/FILES.md", "File Generation") }
                    li { a("docs/BASIC.md", "Basic Markdown") }
                    li { a("docs/EXTENDED.md", "Extended Markdown") }
                }
            }
        }
    }
}