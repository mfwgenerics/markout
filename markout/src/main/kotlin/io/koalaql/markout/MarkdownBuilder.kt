package io.koalaql.markout

import io.koalaql.markout.md.*
import io.koalaql.markout.text.LineWriter

class MarkdownBuilder(
    private val writer: LineWriter
) : Markdown {
    private enum class BuilderState {
        FRESH,
        INLINE,
        AFTER_BLOCK
    }

    private var state: BuilderState = BuilderState.FRESH

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

        MarkdownBuilder(writer).block()
    }

    override fun t(line: MarkdownInline.() -> Unit) = inlined(line)

    override fun t(text: String) = inlined { writer.raw(text) }

    override fun c(text: String) = inlined { writer.raw("`$text`") }

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

    override fun p(block: MarkdownInline.() -> Unit) = blocked(block)

    override fun h1(block: MarkdownInline.() -> Unit) = blocked {
        writer.inline("# ")
        block()
    }

    override fun h2(block: MarkdownInline.() -> Unit) = blocked {
        writer.inline("## ")
        block()
    }

    override fun h3(block: MarkdownInline.() -> Unit) = blocked {
        writer.inline("### ")
        block()
    }

    override fun quote(block: Markdown.() -> Unit) = blocked {
        MarkdownBuilder(writer.prefixed("> ")).block()
    }

    override fun code(code: String) = blocked {
        writer.inline("```")
        writer.newline()
        writer.raw(code)
        writer.newline()
        writer.inline("```")
    }

    override fun ol(builder: MarkdownNumberedList.() -> Unit) {
        var first = true
        var next = 1
        var prefix = ""

        MarkdownNumberedList { number, block ->
            if (!first) writer.newline()
            first = false

            number?.let { next = it }

            val label = "$next. "
            writer.inline(label)

            if (prefix.length != label.length) prefix = " ".repeat(label.length)

            next++

            MarkdownBuilder(writer.prefixed(prefix, start = false)).block()
        }.builder()
    }

    override fun ul(builder: MarkdownDottedList.() -> Unit) = blocked {
        var first = true

        MarkdownDottedList { block ->
            if (!first) writer.newline()
            writer.inline("* ")
            first = false

            MarkdownBuilder(writer.prefixed("  ", start = false)).block()
        }.builder()
    }
}