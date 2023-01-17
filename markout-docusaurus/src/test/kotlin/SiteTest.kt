import io.koalaql.markout.ExecutionMode
import io.koalaql.markout.docusaurus.docusaurus
import io.koalaql.markout.markout
import org.junit.Test
import kotlin.io.path.Path

class SiteTest {
    @Test
    fun `matches test site`() = markout(
        Path("./testing/docs"),
        mode = ExecutionMode.APPLY
    ) {
        docusaurus {
            markdown("intro") {
                h1("Intro")

                p("Text")
            }

            directory("tutorial-basics") {
                label = "Tutorial - Basics"

                link(
                    description = "5 minutes to learn the most important Docusaurus concepts."
                )

                markdown("create-a-page") {
                    h1("Create a Page")

                    code("jsx", "src/pages/my-react-page.js", """
                        import React from 'react';
                        import Layout from '@theme/Layout';

                        export default function MyReactPage() {
                          return (
                            <Layout>
                              <h1>My React page</h1>
                              <p>This is a React page</p>
                            </Layout>
                          );
                        }
                    """.trimIndent())

                    code("mdx", "src/pages/my-markdown-page.md", """
                        # My Markdown page

                        This is a Markdown page
                    """.trimIndent())
                }

                markdown("create-a-document") {
                    code("md", "docs/hello.md", 1..4, """
                        ---
                        sidebar_label: 'Hi!'
                        sidebar_position: 3
                        ---

                        # Hello

                        This is my **first Docusaurus document**!
                    """.trimIndent())
                }
            }

            directory("tutorial-extras") {
                label = "Tutorial - Extras"

                link()
            }
        }
    }
}