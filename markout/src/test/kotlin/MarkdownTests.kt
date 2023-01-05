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
}