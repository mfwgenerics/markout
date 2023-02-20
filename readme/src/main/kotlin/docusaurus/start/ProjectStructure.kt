package docusaurus.start

import docusaurus.pluginRow
import io.koalaql.markout.docusaurus.Docusaurus

import MARKOUT_VERSION
import io.koalaql.kapshot.CapturedBlock
import io.koalaql.markout.Markout
import io.koalaql.markout.buildOutput
import io.koalaql.markout.md.markdown
import io.koalaql.markout.output.Output
import io.koalaql.markout.output.OutputDirectory

const val EXAMPLE_PACKAGE = "myproject"

fun Docusaurus.projectStructure() = markdown("project") {
    h1("Project structure")

    -"Similar to a regular Kotlin application, Markout projects run through a `main` method."
    -"This `main` method is called by Markout when it needs to generate or check files."

    h3("Choosing a plugin")
    -"To start, you choose one of these three plugins based on how much functionality you require."

    table {
        th {
            td("Id")
            td("Functionality")
            td("Link")
        }

        pluginRow("io.koalaql.markout", "Files and folders, generation and check tasks")
        pluginRow("io.koalaql.markout-markdown", "Markdown generation and code capture")
        pluginRow("io.koalaql.markout-docusaurus", "Docusaurus site templating and Markdown support")
    }

    -"For this example we will use the `io.koalaql.markout-markdown` plugin to generate some Markdown docs."

    h3("Configure the plugin")

    -"Apply the Markout plugin in your buildscript and configure the main class."

    code("kotlin", "build.gradle.kts", """
        plugins {
            id("io.koalaql.markout-markdown") version "$MARKOUT_VERSION"
        }
        
        markout {
            /* Main.kt file in the $EXAMPLE_PACKAGE package */
            mainClass = "$EXAMPLE_PACKAGE.MainKt"
        }
    """.trimIndent())

    -"Now define your `main` method"

    lateinit var markoutOutput: OutputDirectory

    fun markout(block: Markout.() -> Unit) {
        markoutOutput = buildOutput(block)
    }

    val captured = CapturedBlock {
        markout {
            markdown("hello") {
                h1("Hello world")
            }
        }
    }

    val packageBlock = """
        package $EXAMPLE_PACKAGE
        
        import io.koalaql.markout.markout
        import io.koalaql.markout.md.markdown
    """.trimIndent()

    code("kotlin", "$packageBlock\n\nfun main() = ${captured.source.text}")
}