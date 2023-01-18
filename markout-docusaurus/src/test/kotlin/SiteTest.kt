import io.koalaql.markout.ExecutionMode
import io.koalaql.markout.docusaurus.docusaurus
import io.koalaql.markout.markout
import org.junit.Test
import kotlin.io.path.Path

class SiteTest {
    @Test
    fun `matches test site`() = markout(
        Path("./testing/docs"),
        mode = ExecutionMode.EXPECT
    ) {
        docusaurus {
            markdown("intro") {
                slug = "/"

                h1("Intro")

                p("Text")
            }

            directory("basics") {
                label = "Basics"

                link(
                    description = "A basic description"
                )

                markdown("code-blocks") {
                    h1("Code Blocks")

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

                markdown("code-with-highlight") {
                    h1("Code With Highlight")

                    code("md", "docs/hello.md", 1..4, """
                        ---
                        sidebar_label: 'Hi!'
                        sidebar_position: 3
                        ---

                        # Hello

                        This is my **first Docusaurus document**!
                    """.trimIndent())
                }

                markdown("admonitions-and-mdx.mdx") {
                    h1("Markdown Features")

                    tip {
                        -"Use this awesome feature option"
                    }

                    danger("Take Care") {
                        -"This action is "+i("dangerous")
                    }

                    raw("""
                        export const Highlight = ({children, color}) => (
                          <span
                            style={{
                              backgroundColor: color,
                              borderRadius: '20px',
                              color: '#fff',
                              padding: '10px',
                              cursor: 'pointer',
                            }}
                            onClick={() => {
                              alert(`You clicked the color ${"$"}{color} with label ${"$"}{children}`)
                            }}>
                            {children}
                          </span>
                        );

                        This is <Highlight color="#25c2a0">Docusaurus green</Highlight> !

                        This is <Highlight color="#1877F2">Facebook blue</Highlight> !
                    """.trimIndent())
                }
            }

            directory("extras") {
                label = "Descriptionless Link"

                link()
            }
        }
    }
}