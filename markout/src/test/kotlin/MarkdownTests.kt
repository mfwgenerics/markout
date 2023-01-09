import io.koalaql.markout.md.markdownString
import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownTests {
    @Test
    fun `mix block and inline at top level`() {
        assertEquals(
            """
            # Example **Header**
            
            This *is* an example
            
            Test2 with some `inline` code
            
            This *is another* example
            
            ```
            Test
            test
            ```
            """.trimIndent(),
            markdownString {
                h1 {
                    t("Example "); b("Header")
                }

                t("This "); i("is"); t(" an example")

                p {
                    t("Test2 with some "); c("inline"); t(" code")
                }

                p {
                    t("This "); i("is another"); t(" example")
                }

                code("Test\ntest")
            }
        )
    }

    @Test
    fun `code blocks in quotes`() {
        assertEquals(
            """
            non-quote

            > Quote
            > 
            > > nested quote
            > 
            > > quote with `code` in it
            > > 
            > > ```
            > > Multi
            > > Line
            > > Code
            > > Block
            > > ```
            """.trimIndent(),
            markdownString {
                p {
                    t("non-quote")
                }

                quote {
                    t("Quote")

                    quote("nested quote")

                    quote {
                        t("quote with "); c("code"); t(" in it")

                        code("Multi\nLine\nCode\nBlock")
                    }
                }
            }
        )
    }

    @Test
    fun `unordered lists`() {
        assertEquals(
            """
            > A list
            > 
            > * test
            > * uhhh
            >   
            >   something else
            > * * 1
            >   * 2
            """.trimIndent(),
            markdownString {
                quote {
                    t("A list")

                    ul {
                        li("test")

                        li {
                            p {
                                t("uhhh")
                            }
                            p {
                                t("something else")
                            }
                        }

                        li {
                            ul {
                                li("1")
                                li("2")
                            }
                        }
                    }
                }
            }
        )
    }

    @Test
    fun `ordered list prefix length`() {
        val giantList = markdownString {
            ol {
                repeat(100) {
                    li("two\nlines")
                }
            }
        }.lines()

        assertEquals(
            """
            9. two
               lines
            10. two
                lines
            """.trimIndent(),
            giantList.subList(16, 20).joinToString("\n")
        )

        assertEquals(
            """
            98. two
                lines
            99. two
                lines
            100. two
                 lines
            """.trimIndent(),
            giantList.subList(194, 200).joinToString("\n")
        )
    }

    @Test
    fun `arbitrary ordered list`() {
        assertEquals(
            """
            0. two
               lines
            1. two
               lines
            2. two
               lines
            1000. two
                  lines
            1. two
               lines
            """.trimIndent(),
            markdownString {
                ol {
                    li(0, "two\nlines")
                    li("two\nlines")
                    li("two\nlines")
                    li(1000, "two\nlines")
                    li(1, "two\nlines")
                }
            }
        )
    }

    @Test
    fun `check lists`() {
        assertEquals(
            """
            Checklists!

            - [x] Task 1
            - [x] Task *2*?
            - [ ] Task 3
            """.trimIndent(),
            markdownString {
                t("Checklists!")

                cl {
                    li(true, "Task 1")
                    li(true) { t("Task "); i("2"); t("?") }
                    li(false, "Task 3")
                }
            }
        )
    }

    @Test
    fun `links and citations`() {
        assertEquals(
            """
            # Link *in* [Header](https://example.com)

            > How about a [reference style link][1]

            [same reference style link again (should dedup)][1]

            [won't dedup][2]

            > [will dedup][2]
            > 
            > [won't dedup][3]

            [1]: https://example.com/something
            [2]: https://example.com/something "Something1"
            [3]: https://example.com/something "Something 2"
            """.trimIndent(),
            markdownString {
                h1 {
                    t("Link "); i("in"); t(" "); a("https://example.com", "Header")
                }

                quote {
                    t("How about a ")
                    a(cite("https://example.com/something"), "reference style link")
                }

                p {
                    a(cite("https://example.com/something"), "same reference style link again (should dedup)")
                }

                p { a(cite("https://example.com/something", "Something1"), "won't dedup") }

                quote {
                    p { a(cite("https://example.com/something", "Something1"), "will dedup") }
                    p { a(cite("https://example.com/something", "Something 2"), "won't dedup") }
                }
            }
        )
    }

    @Test
    fun `tables with references`() {
        assertEquals(
            """
            # Table

            | Header 1                                                | Header 2          |
            | ------------------------------------------------------- | ----------------- |
            | Cell 1                                                  | *Italicized cell* |
            | [Very long cell name that also links to a reference][1] | Cell 2            |

            # More compact table

            | Cell 1     | *icell* |
            | [Short][1] | Cell 2  |

            [1]: https://example.com
            """.trimIndent(),
            markdownString {
                h1("Table")

                table {
                    th { td("Header 1"); td("Header 2") }
                    tr { td("Cell 1"); td { i("Italicized cell") } }
                    tr {
                        td {
                            a(cite("https://example.com"), "Very long cell name that also links to a reference")
                        }
                        td("Cell 2");
                    }
                }

                h1("More compact table")

                table {
                    tr { td("Cell 1"); td { i("icell") } }
                    tr {
                        td {
                            a(cite("https://example.com"), "Short")
                        }
                        td("Cell 2");
                    }
                }
            }
        )
    }

    @Test
    fun `footnotes with references`() {
        assertEquals(
            """
            test[^1]
            
            this isn't a [footnote][1][^2][^3]
            
            [^1]: Test note
            [^2]: complex footnote that references [^4] ~~another footnote~~
                  
                  also it has multiple paragraphs and a [link][1]
                  
                  > and quoted text
            [^3]: [the link is deduped in this last footnote][1]
            [^4]: Example footnote
            
            [1]: https://example.com
            """.trimIndent(),
            markdownString {
                p {
                    t("test"); footnote("Test note")
                }

                t("this isn't a ")
                a(cite("https://example.com"), "footnote")

                footnote {
                    p {
                        t("complex footnote that references ");
                        footnote("Example footnote")
                        t(" ")
                        s("another footnote")
                    }

                    p {
                        t("also it has multiple paragraphs and a ")
                        a(cite("https://example.com"), "link")
                    }

                    quote {
                        t("and quoted text")
                    }
                }

                footnote {
                    a(cite("https://example.com"), "the link is deduped in this last footnote")
                }
            }
        )
    }

    @Test
    fun `code block delimiter`() {
        assertEquals(
            """
            ````
            ```
            ````
            """.trimIndent(),
            markdownString { code("```") }
        )

        assertEquals(
            """
            `````
            ````
            `````
            """.trimIndent(),
            markdownString { code("````") }
        )

        assertEquals(
            """
            ``````
            ``` ````` ``
            ``````
            """.trimIndent(),
            markdownString { code("``` ````` ``") }
        )
    }
}