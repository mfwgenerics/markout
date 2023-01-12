import io.koalaql.markout.text.AppendableLineWriter
import io.koalaql.markout.text.LineWriter
import kotlin.test.Test
import kotlin.test.assertEquals

class LineWriterTests {
    @Test
    fun `raw text splitting`() {
        fun test(case: String) {
            val sb = StringBuilder()

            val lw = object : LineWriter {
                override fun inline(text: String) { sb.append(text) }
                override fun newline() { sb.append("\n") }
            }

            lw.raw(case)

            assertEquals(case, "$sb")
        }

        test("\nlines\n\n\n \n more")
        test("  lines more")
        test("")
    }

    @Test
    fun `newline trimming`() {
        fun trimLines(case: String): String = "${StringBuilder().also {
            AppendableLineWriter(it)
                .trimmedLines()
                .raw(case)
        }}"

        fun assertTrimmed(expected: String, case: String) {
            val trimmed = trimLines(case)

            assertEquals(expected, trimmed)
            assertEquals(trimmed, trimLines(trimmed)) // idempotent
        }

        assertTrimmed("", trimLines("\n\n\n"))

        assertTrimmed("a", trimLines("\na\n\n"))
        assertTrimmed("a\n\nb", trimLines("\n\na\n\nb\n\n"))
        assertTrimmed("  a", trimLines("  \n \t\n  a\n  \n  "))
    }

    @Test
    fun `paragraph rules`() {
        fun paragraphize(case: String): String = "${StringBuilder().also {
            AppendableLineWriter(it)
                .paragraphRules()
                .raw(case)
        }}"

        assertEquals("", paragraphize(""))
        assertEquals("", paragraphize("     "))
        assertEquals("", paragraphize("     \n\n\n"))
        assertEquals("a  \n", paragraphize("     \n    a  \n\n"))
        assertEquals("""
            I'm an
            untrimmed paragraph
            with alignments all off
            a markdown line break  
            (i.e. previous space has two whitespace after)
            
        """.trimIndent(), paragraphize("""
           I'm an
               untrimmed paragraph
               
       with alignments all off
       
       a markdown line break  
         (i.e. previous space has two whitespace after)
         
         
        """))
    }
}