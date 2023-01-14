import io.koalaql.markout.Markout
import io.koalaql.markout.buildOutput
import io.koalaql.markout.md.markdown
import java.nio.file.Path
import kotlin.io.path.Path

fun Markout.fileGen() = markdown("FILES") {
    h1("File Generation")

    -"Markout can run in one of two modes"

    sectioned {
        section("Apply Mode") {
            -"Apply is the default mode. It generates files and directories and deletes"
            -"previously generated files and directories that were not regenerated."

            h3("Files and Directories")

            -"Markout provides a straightforward DSL for generating files and directories"

            fun markout(ignored: Path, builder: Markout.() -> Unit) =
                buildOutput(builder)

            val output = code {
                markout(Path("..")) {
                    directory("my-directory") {
                        directory("inner") {
                            file("inner.txt", "another plain text file")
                        }

                        file("plain.txt", "the contents of a plain text file")

                        file("circle.svg", """
                            <svg height="100" width="100">
                                <circle cx="50" cy="50" r="40" fill="black" />
                            </svg> 
                        """.trimIndent())
                    }

                    markdown("readme") {
                        -"A markdown file"
                        -"The .MD prefix is automatically added to the filename"
                    }
                }
            }

            -"When this code is run it generates the following file tree"

            code(drawFileTree(output))

            h3("File Tracking")

            p {
                -"When Markout generates directories it includes a `.markout` file. This is"
                -"how Markout keeps track of generated files. It should always be checked"
                -"into git. Markout will never change or delete an existing file or directory"
                -"unless it is tracked in `.markout`"
            }

            p {
                -"File tracking allows regular files to be mixed in with generated ones."
                -"For example, you might mix handwritten markdown into your docs directory."
            }
        }

        section("Check Mode") {

        }
    }
}