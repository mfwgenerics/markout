import io.koalaql.markout.Markout
import io.koalaql.markout.md.markdown

fun Markout.extended() = markdown("EXTENDED") {
    h1("Extended Syntax")

    h2("Tables")

    example {
        table {
            th {
                td("Column 1")
                td { i("Italic Column") }
            }

            tr {
                td("1997")
                td("Non-italic")
            }

            tr {
                td("2023")
                td { i("Italic") }
            }
        }
    }

    h2("Footnotes")

    example {
        fun note() = footnote("""
            At the moment there is no way to re-use footnotes
            and the requirement for the note text to appear at
            the site of the footnote call is less than ideal
        """.trimIndent())

        +"The syntax is a work in progress" + note() + " but footnotes are possible."
    }
}