import io.koalaql.markout.Markout
import io.koalaql.markout.md.markdown

fun Markout.basic() = markdown("BASIC") {
    h1("Basic Syntax")

    h2("Headers")

    example {
        h1("Header 1")
        h2("Header 2")
        h3("Header 3")
        h4("Header 4")
        h5("Header 5")
        h6("Header 6")
    }

    h2("Paragraphs")

    example {
        p {
            +"This text will appear in a paragraph."
            +" "
            +"This sentence will be grouped with the preceding one."
        }

        p("""
            A second paragraph.
            Line breaks won't
            affect the markdown layout
        """.trimIndent())
    }

    h2("Emphasis")

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

    h2("Blockquotes")

    example {
        +"I'm about to quote something"

        quote {
            +"Here's the quote with a nested quote inside"

            quote {
                +"A final inner quote"
            }
        }
    }

    h2("Lists")

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

    h2("Code")

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

    h2("Horizontal Rules")

    example {
        t("Separated")
        hr()
        t("By")
        hr()
        t("Hrs")
    }

    h2("Links")

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