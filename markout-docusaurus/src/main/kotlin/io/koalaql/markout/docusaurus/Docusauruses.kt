package io.koalaql.markout.docusaurus

import io.koalaql.markout.MarkdownBuilder
import io.koalaql.markout.Markout
import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.md.markdown
import io.koalaql.markout.md.markdownString
import io.koalaql.markout.text.AppendableLineWriter

private fun docusaurusMdFile(
    position: Int,
    builder: DocusaurusMarkdownFile.() -> Unit
): String {
    val sb = StringBuilder()

    lateinit var impl: MarkdownFileImpl
    val lw = AppendableLineWriter(sb)

    impl = MarkdownFileImpl(MarkdownBuilder(lw, top = true))

    impl.sidebar(position)
    impl.builder()

    if (sb.isEmpty()) return ""

    sb.append("\n")

    return "$sb"
}

private class DirectoryContext(
    private val markout: Markout,
    private val position: Int
): DocusaurusDirectory {
    private var sidebarPosition = 0

    override var label: String = ""

    private var linkImpl: String? = null

    override fun link(description: String) {
        linkImpl = description
    }

    override fun directory(name: String, builder: DocusaurusDirectory.() -> Unit) {
        val position = ++sidebarPosition
        markout.directory(name) {
            val ctx = DirectoryContext(this, position)
            ctx.builder()
            ctx.category()
        }
    }

    override fun file(name: String, contents: String) {
        markout.file(name, contents)
    }

    override fun markdown(name: String, builder: DocusaurusMarkdownFile.() -> Unit) {
        val position = ++sidebarPosition

        markout.markdown(name, docusaurusMdFile(position, builder))
    }

    fun category() {
        markout.file("_category_.json", writeJson(indent = "  ") {
            braces {
                if (label.isNotBlank()) "label" - label
                "position" - position

                if (linkImpl != null) {
                    "link" braces {
                        "type" - "generated-index"

                        linkImpl?.takeIf { it.isNotBlank() }?.let {
                            "description" - it
                        }
                    }
                }
            }

            write("\n")
        })
    }
}

@MarkoutDsl
fun Markout.docusaurus(block: Docusaurus.() -> Unit) {
    object : Docusaurus {
        private var sidebarPosition = 0

        override fun directory(name: String, builder: DocusaurusDirectory.() -> Unit) {
            val position = ++sidebarPosition

            this@docusaurus.directory(name) {
                val ctx = DirectoryContext(this, position)
                ctx.builder()
                ctx.category()
            }
        }

        override fun file(name: String, contents: String) {
            this@docusaurus.file(name, contents)
        }

        override fun markdown(name: String, builder: DocusaurusMarkdownFile.() -> Unit) {
            val position = ++sidebarPosition

            this@docusaurus.markdown(name, docusaurusMdFile(position, builder))
        }
    }.block()
}