import io.koalaql.markout.markout
import io.koalaql.markout.md.markdown
import io.koalaql.markout.md.markdownString
import kotlin.io.path.Path

fun main() = markout(Path("..")) {
    directory("docs") {
        basic()
        extended()
    }

    markdown("README") {
        h1("Markout")

        t("Markout is a library for generating markdown files and directories from Kotlin")

        h2("Syntax")

        ol {
            li { a("docs/BASIC.md", "Basic Syntax") }
            li { a("docs/EXTENDED.md", "Extended Syntax") }
        }

        h2("Example")

        val exampleMarkdown = markdownString {
            this@markdown.code("kotlin") {
                h1 { t("Hello "); b("Markout!") }

                p("Example paragraph")

                ol {
                    li("List")
                    li("Of")
                    li("Items")
                }
            }
        }

        p("Will produce the following markdown")

        code("md", exampleMarkdown)
    }
}