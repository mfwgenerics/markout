package io.koalaql.markout.docusaurus

import io.koalaql.markout.Markout
import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.md.markdownTo
import io.koalaql.markout.md.withMdSuffix
import io.koalaql.markout.text.AppendableLineWriter
import java.io.OutputStream
import kotlin.io.path.Path
import kotlin.io.path.name

private fun docusaurusMdFile(
    output: OutputStream,
    position: Int,
    builder: DocusaurusMarkdownFile.() -> Unit
) {
    lateinit var impl: MarkdownFileImpl

    val writer = output.writer()
    val lw = AppendableLineWriter(writer)

    markdownTo(lw.onWrite {
        lw.raw(impl.header())
        lw.newline()
    }) {
        impl = MarkdownFileImpl(this, position)

        impl.builder()
    }

    lw.newline()

    writer.flush()
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

        markout.file(withMdSuffix(name)) {
            docusaurusMdFile(it, position, builder)
        }
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
fun Markout.docusaurus(block: DocusaurusRoot.() -> Unit) {
    object : DocusaurusRoot {
        override fun configure(block: DocusaurusSettings.() -> Unit) {
            this@docusaurus.directory(this@docusaurus.untracked("static")) {
                file(untracked(".nojekyll"), "")
            }

            fun copyResource(path: String, name: String = Path(path).name) {
                this@docusaurus.file(this@docusaurus.untracked(name)) { out ->
                    Resources.open(path).use { it.copyTo(out) }
                }
            }

            this@docusaurus.file(this@docusaurus.untracked("docusaurus.config.js")) {
                val writer = it.writer()

                buildConfigJs(
                    AppendableLineWriter(writer),
                    block
                )

                writer.flush()
            }

            copyResource("/bootstrap/gitignore", ".gitignore")
            copyResource("/bootstrap/babel.config.js")
            copyResource("/bootstrap/package.json")
            copyResource("/bootstrap/sidebars.js")
            copyResource("/bootstrap/tsconfig.json")
            copyResource("/bootstrap/yarn.lock")
            copyResource("/bootstrap/linux.yarnrc")
        }

        override fun docs(block: Docusaurus.() -> Unit) {
            this@docusaurus.directory(this@docusaurus.untracked("docs")) {
                val markout = this

                object : Docusaurus {
                    private var sidebarPosition = 0

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

                        markout.file(withMdSuffix(name)) {
                            docusaurusMdFile(it, position, builder)
                        }
                    }
                }.block()
            }
        }
    }.block()
}

@MarkoutDsl
fun Markout.docusaurus(name: String, block: DocusaurusRoot.() -> Unit) =
    directory(name) {
        docusaurus(block)
    }