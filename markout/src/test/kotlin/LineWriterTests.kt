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
}