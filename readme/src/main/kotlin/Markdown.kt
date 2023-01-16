import io.koalaql.kapshot.CapturedBlock
import io.koalaql.markout.Markout
import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.md.Markdown
import io.koalaql.markout.md.markdown

@MarkoutDsl
fun <T> Markdown.code(lang: String, code: CapturedBlock<T>): T {
    code(lang, code.source.text)

    return code()
}

@MarkoutDsl
fun <T> Markdown.code(code: CapturedBlock<T>): T = code("kotlin", code)

fun Markout.markdownDocs() = markdown("MARKDOWN") {
    h1("Markdown")

    sectioned {
        section("Headers") {
            example {
                h1("Header 1")
                h2("Header 2")
                h3("Header 3")
                h4("Header 4")
                h5("Header 5")
                h6("Header 6")
            }
        }

        section("Paragraphs") {
            example {
                p {
                    -"This text will appear in a paragraph."

                    -"This sentence will be grouped with the preceding one."
                }

                p("""
                    A second paragraph.
                    Line breaks won't
                    affect the rendered markdown
                    and indent is trimmed
                """)
            }
        }

        section("Emphasis") {
            example {
                p {
                    b("Bold")
                    +", "
                    i("Italics")
                    +" and "
                    b { i("Bold italics") }
                    +"."
                }

                p {
                    /* same as above, using `+` syntax sugar */
                    b("Bold") + ", " + i("Italics") + " and " + b { i("Bold italics") } + "."
                }

                p {
                    +"For inline text styling you can *still* use **raw markdown**"
                }
            }
        }

        section("Blockquotes") {
            example {
                +"I'm about to quote something"

                quote {
                    +"Here's the quote with a nested quote inside"

                    quote {
                        +"A final inner quote"
                    }
                }
            }
        }

        section("Lists") {
            example {
                +"Dot points"

                ul {
                    li("Dot point 1")
                    li("Another point")
                    li("A third point")
                }

                +"Numbered"
                ol {
                    li("Item 1")
                    li {
                        p {
                            +"You can nest any markdown inside list items"
                        }

                        p {
                            +"Multiple paragraphs"
                        }

                        quote {
                            +"Or even a quote"
                        }
                    }
                    li {
                        ol {
                            li("This includes")
                            li("Lists themselves")
                        }
                    }
                }

                +"Task lists"
                cl {
                    li(true, "Create a markdown DSL")
                    li(true, "Add task list support")
                    li(false, "Solve all of the world's problems")
                }
            }
        }

        section("Code") {
            example {
                c("Inline code block")

                code("multiline\ncode\nblocks")

                code("kotlin", """
                    fun main() {
                        println("Syntax hinted code!")
                    }
                """.trimIndent())

                val result = code {
                    /* this code block runs */
                    fun square(x: Int) = x*x

                    square(7)
                }

                +"Code executed with result: "
                c("$result")
            }
        }

        section("Horizontal Rules") {
            example {
                t("Separated")
                hr()
                t("By")
                hr()
                t("Hrs")
            }
        }

        section("Links") {
            example {
                p {
                    +"Visit "
                    a("https://example.com", "Example Website")
                }

                p {
                    a("https://example.com") {
                        +"Links "
                        i("can contain")
                        +" "
                        b("inner formatting")
                    }
                }

                p {
                    a(cite("https://example.com"), "Reference style link")
                }

                p {
                    +"Reference "
                    a(cite("https://example.com"), "links")
                    +" are de-duplicated"
                }

                p {
                    a(cite("https://example.com", "Example"), "References")
                    +" can be titled"
                }
            }
        }

        section("Images") {
            example {
                p {
                    +"In inline contexts images will "
                    img("markout.png")
                    +" be shown inline "
                    img("markout.png", "Alt text", "Title text is displayed on hover")
                }

                +"At top level images will be treated as blocks and vertically separated"
                img("markout.png")
                img("markout.png")
                img("unknown.png", "Alt text is displayed when the image can't be displayed load")
            }
        }

        section("Tables") {
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
        }

        section("Footnotes") {
            example {
                fun note() = footnote("""
                    At the moment there is no way to re-use footnotes
                    and the requirement for the note text to appear at
                    the site of the footnote call is less than ideal
                """)

                +"The syntax is a work in progress" + note() + " but footnotes are possible."
            }
        }
    }
}