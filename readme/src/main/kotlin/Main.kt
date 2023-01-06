import io.koalaql.markout.markout
import io.koalaql.markout.md.markdown
import io.koalaql.markout.md.markdownString
import kotlin.io.path.Path

fun main() = markout(Path("..")) {
    directory("docs") {

    }

    markdown("README") {
        t("Markout is a library for generating markdown files and directories from Kotlin")

        h1("Example")

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