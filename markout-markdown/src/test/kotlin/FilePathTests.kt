import io.koalaql.markout.buildOutput
import io.koalaql.markout.md.markdown
import io.koalaql.markout.output.OutputDirectory
import io.koalaql.markout.text.AppendableLineWriter
import io.koalaql.markout.text.LineWriter
import kotlin.test.Test
import kotlin.test.assertEquals

class FilePathTests {
    private fun extractPaths(output: OutputDirectory, out: LineWriter) {
        output.entries().forEach { (name, entry) ->
            out.inline(name)
            out.newline()

            val output = entry.output

            if (output is OutputDirectory) {
                extractPaths(output, out.prefixed("$name/"))
            }
        }
    }

    private fun extractPaths(output: OutputDirectory) = "${StringBuilder().also {
        extractPaths(output, AppendableLineWriter(it).trimmedLines())
    }}"

    @Test
    fun `md suffix handling`() {
        assertEquals("""
            test-dir
            test-dir/test.md
            test-dir/test.mdx
            test-dir/nested
            test-dir/nested/some-file
            test-dir/nested/test.md.md
            test-dir/test3.md
            test.config.md
            some-file.txt
        """.trimIndent(),
            extractPaths(buildOutput {
                directory("test-dir") {
                    markdown("test.md") { }
                    markdown("test.mdx") { }

                    directory("nested") {
                        file("some-file", "")
                        markdown("test.md.md") { }
                    }

                    markdown("test3") { }
                }

                markdown("test.config") { }
                file("some-file.txt", "")
            })
        )
    }
}