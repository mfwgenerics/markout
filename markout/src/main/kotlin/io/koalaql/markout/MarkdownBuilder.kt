package io.koalaql.markout

import io.koalaql.markout.md.*
import io.koalaql.markout.text.AppendableLineWriter
import io.koalaql.markout.text.LineWriter

class MarkdownBuilder(
    private val writer: LineWriter,
    private val bibliography: Bibliography = Bibliography()
) : Markdown {
    private enum class BuilderState {
        FRESH,
        INLINE,
        AFTER_BLOCK
    }

    private var state: BuilderState = BuilderState.FRESH

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
        if (state == BuilderState.AFTER_BLOCK) {
            writer.newline()
            writer.newline()
        }

        state = BuilderState.INLINE

        line()
    }

    private fun blocked(block: MarkdownBuilder.() -> Unit) {
        if (state == BuilderState.AFTER_BLOCK || state == BuilderState.INLINE) {
            writer.newline()
            writer.newline()
        }

        state = BuilderState.AFTER_BLOCK

        MarkdownBuilder(writer, bibliography).block()
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

    override fun p(block: MarkdownInline.() -> Unit) = blocked(block)

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
        var first = true
        var prefix = ""

        builder { label, block ->
            if (!first) writer.newline()
            first = false

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

        if (rows.isNotEmpty()) blocked {
            var first = true

            rows.forEach { row ->
                if (!first) {
                    writer.newline()
                } else {
                    first = false
                }

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

        if (footnotes.isNotEmpty()) {
            writer.newline()

            var i = 0

            while (i < footnotes.size) {
                /* no forEach - list may be appended to during loop */

                val (label, write) = footnotes[i++]

                writer.newline()
                writer.inline(label)
                writer.inline(": ")

                val indent = " ".repeat(label.length + 2)

                MarkdownBuilder(writer.prefixed(indent, start = false), bibliography).write()
            }
        }

        if (references.isNotEmpty()) {
            writer.newline()

            references.forEach { (reference, cite) ->
                writer.newline()
                writer.inline(cite.label)
                writer.inline(": ")
                writer.inline(reference)
            }
        }
    }
}