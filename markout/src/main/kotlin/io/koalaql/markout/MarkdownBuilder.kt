package io.koalaql.markout

import io.koalaql.markout.md.*
import io.koalaql.markout.text.AppendableLineWriter
import io.koalaql.markout.text.LineWriter

class MarkdownBuilder(
    private val writer: LineWriter,
    private val bibliography: Bibliography = Bibliography()
) : Markdown {
    private sealed interface BuilderState {
        object Fresh : BuilderState
        object AfterBlock : BuilderState
        class Inline(
            val builder: MarkdownBuilder
        ) : BuilderState
    }

    private var state: BuilderState = BuilderState.Fresh

    override fun cite(href: String, title: String?): Citation {
        val sb = StringBuilder()

        sb.append(href)

        if (title != null) {
            sb.append(" \"")
            sb.append(title)
            sb.append("\"")
        }

        return bibliography.reference("$sb")
    }

    override fun footnote(note: Markdown.() -> Unit) = inlined {
        writer.inline(bibliography.footnote(note))
    }

    private fun inlined(line: MarkdownBuilder.() -> Unit) {
        val builder = when (val current = state) {
            is BuilderState.Inline -> current.builder
            else -> {
                val writer = if (current == BuilderState.AfterBlock) {
                    writer.onWrite {
                        writer.newline()
                        writer.newline()
                    }
                } else {
                    writer
                }

                val builder = MarkdownBuilder(writer.trimmedLines().paragraphRules(), bibliography)

                state = BuilderState.Inline(builder)

                builder
            }
        }

        builder.line()
    }

    private fun blocked(block: MarkdownBuilder.() -> Unit) {
        val writer = if (state == BuilderState.AfterBlock || state is BuilderState.Inline) {
            state = BuilderState.AfterBlock

            writer.onWrite {
                writer.newline()
                writer.newline()
            }
        } else {
            writer.onWrite {
                state = BuilderState.AfterBlock
            }
        }

        MarkdownBuilder(writer.trimmedLines(), bibliography).block()
    }

    override fun t(line: MarkdownInline.() -> Unit) = inlined(line)

    override fun t(text: String) = inlined { writer.raw(text) }

    override fun c(text: String) = inlined { writer.raw("`$text`") }

    private fun link(href: String, line: MarkdownInline.() -> Unit) = inlined {
        writer.inline("[")
        line()
        writer.inline("]")
        writer.inline(href)
    }

    override fun a(href: String, line: MarkdownInline.() -> Unit) {
        link("($href)", line)
    }

    override fun a(href: Citation, line: MarkdownInline.() -> Unit) {
        link(href.label, line)
    }

    override fun i(block: MarkdownInline.() -> Unit) = inlined {
        writer.inline("*")
        block()
        writer.inline("*")
    }

    override fun b(block: MarkdownInline.() -> Unit) = inlined {
        writer.inline("**")
        block()
        writer.inline("**")
    }

    override fun s(block: MarkdownInline.() -> Unit) = inlined {
        writer.inline("~~")
        block()
        writer.inline("~~")
    }

    override fun p(block: MarkdownBlock.() -> Unit) = blocked {
        MarkdownBuilder(writer.paragraphRules(), bibliography).block()
    }

    override fun hr() = blocked {
        writer.inline("---")
    }

    override fun h(level: Int, block: MarkdownInline.() -> Unit) = blocked {
        writer.inline("#".repeat(level))
        writer.inline(" ")
        block()
    }

    override fun quote(block: Markdown.() -> Unit) = blocked {
        MarkdownBuilder(writer.prefixed("> "), bibliography).block()
    }

    override fun code(lang: String, code: String) = blocked {
        var delimiter = "```"

        /* inefficient but good enough up to 10k backtick runs */
        while (code.contains(delimiter)) delimiter = "$delimiter`"

        writer.inline(delimiter)
        writer.inline(lang)
        writer.newline()
        writer.raw(code)
        writer.newline()
        writer.inline(delimiter)
    }

    private fun interface GenericList {
        fun li(label: String, block: Markdown.() -> Unit)
    }

    private fun list(builder: GenericList.() -> Unit) = blocked {
        var prefix = ""

        builder { label, block ->
            writer.newline()
            writer.inline(label)

            if (prefix.length != label.length) prefix = " ".repeat(label.length)

            MarkdownBuilder(writer.prefixed(prefix, start = false), bibliography).block()
        }
    }

    override fun ol(builder: MarkdownNumberedList.() -> Unit) = list {
        var next = 1

        builder { number, block ->
            number?.let { next = it }
            li("${next++}. ", block)
        }
    }

    override fun ul(builder: MarkdownDottedList.() -> Unit) = list {
        builder {
            li("* ", it)
        }
    }

    override fun cl(builder: MarkdownCheckList.() -> Unit) = list {
        builder { checked, block ->
            li(if (checked) "- [x] " else "- [ ] ", block)
        }
    }

    private class Row(
        val cells: List<String>,
        val pad: String
    )

    override fun table(builder: MarkdownTable.() -> Unit) {
        val rows = arrayListOf<Row>()

        val lengths = arrayListOf<Int>()

        fun row(cells: List<String>, pad: String) {
            if (cells.isEmpty()) return

            cells.forEachIndexed { ix, it ->
                /* TODO something less ASCII-brained than String.length? */
                if (lengths.size == ix) {
                    lengths.add(it.length)
                } else {
                    lengths[ix] = maxOf(lengths[ix], it.length)
                }
            }

            rows.add(Row(cells, pad))
        }

        fun cells(row: MarkdownTableRow.() -> Unit): List<String> = arrayListOf<String>().apply {
            row {
                val sb = StringBuilder()
                MarkdownBuilder(AppendableLineWriter(sb), bibliography).it()
                add("$sb")
            }
        }

        object : MarkdownTable {
            override fun th(row: MarkdownTableRow.() -> Unit) {
                val cells = cells(row)

                row(cells, " ")
                row(cells.map { "---" }, "-")
            }

            override fun tr(row: MarkdownTableRow.() -> Unit) {
                row(cells(row), " ")
            }
        }.builder()

        blocked {
            rows.forEach { row ->
                writer.newline()
                writer.inline("|")

                row.cells.forEachIndexed { ix, it ->
                    writer.inline(" ")
                    writer.inline(it)
                    writer.inline(row.pad.repeat(lengths[ix] - it.length))
                    writer.inline(" |")
                }
            }
        }
    }

    fun footer() {
        val footnotes = bibliography.footnotes
        val references = bibliography.references

        list {
            var i = 0

            while (i < footnotes.size) {
                /* no forEach - list may be appended to during loop */

                val (label, write) = footnotes[i++]

                li("$label: ", write)
            }
        }

        list {
            references.forEach { (reference, cite) ->
                li("${cite.label}: ") { +reference }
            }
        }
    }
}