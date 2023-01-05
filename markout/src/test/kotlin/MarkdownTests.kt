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
}