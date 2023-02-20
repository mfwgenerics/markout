package docusaurus

import drawFileTree
import io.koalaql.markout.Markout
import io.koalaql.markout.buildOutput
import io.koalaql.markout.docusaurus.Docusaurus
import io.koalaql.markout.docusaurus.DocusaurusMarkdown
import io.koalaql.markout.docusaurus.docusaurus
import io.koalaql.markout.md.markdown
import io.koalaql.markout.output.Output
import io.koalaql.markout.output.OutputDirectory
import io.koalaql.markout.output.OutputEntry
import io.koalaql.markout.output.OutputFile
import java.io.ByteArrayOutputStream

private fun drawProjectFileTree(output: Output) = drawFileTree(object : OutputDirectory {
    override fun entries(): Map<String, OutputEntry> = mapOf("my-project" to OutputEntry(
        tracked = false,
        output
    ))
})

private fun DocusaurusMarkdown.sourceCaptureExample() {
    lateinit var markoutOutput: Pair<String, String>

    fun markout(builder: Markout.() -> Unit) {
        markoutOutput = buildOutput(builder).entries().mapValues { entry ->
            with (entry.value.output as OutputFile) {
                ByteArrayOutputStream()
                    .also { writeTo(it) }
                    .toByteArray()
                    .toString(Charsets.UTF_8)
                    .trim()
            }
        }.entries.map { (x,y) -> x to y }.first()
    }

    val source = execBlock {
        markout {
            markdown("EXAMPLE.md") {
                h2("Sample Code")

                val block = code {
                    fun square(x: Int) = x*x

                    square(7)
                }

                p {
                    -"The code above results in `${block.invoke()}`."
                }

                p {
                    -"Source file and line numbers of code blocks are also captured."
                }
            }
        }
    }

    tabbed(imports = true, mapOf(
        "Main.kt" to {
            code("kotlin", source)
        },
        markoutOutput.first to {
            code("markdown", markoutOutput.second)
        },
        "Rendered" to {
            quote {
                raw(markoutOutput.second)
            }
        }
    ))
}

private fun DocusaurusMarkdown.docusaurusExample() {
    lateinit var fileTree: String

    fun markout(builder: Markout.() -> Unit) {
        fileTree = drawProjectFileTree(buildOutput(builder))
    }

    val source = execBlock {
        markout {
            docusaurus("my-site") {
                configure {
                    title = "Example Site"
                }

                docs {
                    markdown("hello.md") {
                        h1("Hello Docusaurus!")
                    }
                }
            }
        }
    }

    tabbed(imports = false, mapOf(
        "Main.kt" to {
            code("kotlin", source)
        },
        "Generated Files" to {
            code(fileTree)
        }
    ))
}

fun Docusaurus.intro() = markdown("intro") {
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
        -" "+a(cite("https://docusaurus.io/"), "Docusaurus")+" static site generator to quickly build"
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
        -"Markout is designed to integrate with "+a(cite("https://github.com/mfwgenerics/kapshot"), "Kapshot")+","
        -"a minimal Kotlin compiler plugin that allows source code to be"
        -"captured and inspected at runtime."
        -"Kapshot is the magic ingredient that enables fully executable and testable sample code blocks."
    }

    h2("How it works")

    -"Markout is designed around a core file generation layer that allows file trees to be declared in code."
    -"These file trees are reconciled into a target directory, which is your project root directory by default."
    -"Through extra plugins and libraries, the file generation layer can be extended with functionality for"
    -"generating markdown, capturing source code and building Docusaurus websites."

    h3("File generation")

    -"Markout generates files by running code from Kotlin projects with the Markout Gradle plugin applied."
    -"You supply a `main` method which invokes a `markout` block to describe how files should be generated."
    -"This code runs every time files are generated or verified."

    lateinit var markoutOutput: OutputDirectory

    fun markout(builder: Markout.() -> Unit) {
        markoutOutput = buildOutput(builder)
    }

    code("kotlin", "Main.kt", "fun main() = ${execBlock {
        markout {
            file("README.txt", "Hello world!")

            directory("docs") {
                file("INTRO.txt", "Another text file!")
                file("OUTRO.txt", "A final text file")
            }
        }
    }}")

    -"When the code above is run using `:markout`, it generates the following file tree"
    -"and creates it in the project directory."

    code(drawProjectFileTree(markoutOutput))

    -"The `:markoutCheck` task then verifies that these files match subsequent runs of the code."

    h3("Markdown")

    -"The Markdown plugin provides a DSL for generating Markdown files and strings."
    -"Markdown code blocks can directly capture source code which allows you to execute and test sample code."

    sourceCaptureExample()

    h3("Docusaurus")

    -"The Docusaurus plugin provides a `docusaurus` builder and Gradle tasks for building and running a "
    a(cite("https://docusaurus.io/"), "Docusaurus")+" site. This plugin is how these docs are generated."

    docusaurusExample()
}