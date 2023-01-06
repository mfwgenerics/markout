package io.koalaql.markout

import io.koalaql.markout.md.*
import io.koalaql.markout.text.LineWriter

class MarkdownBuilder(
    private val writer: LineWriter,
    private val citations: MutableMap<String, Citation>
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

        return citations.getOrPut("$sb") {
            Citation("[${citations.size + 1}]")
        }
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

        MarkdownBuilder(writer, citations).block()
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
        MarkdownBuilder(writer.prefixed("> "), citations).block()
    }

    override fun code(lang: String, code: String) = blocked {
        writer.inline("```")
        writer.inline(lang)
        writer.newline()
        writer.raw(code)
        writer.newline()
        writer.inline("```")
    }

    override fun ol(builder: MarkdownNumberedList.() -> Unit) = blocked {
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

            MarkdownBuilder(writer.prefixed(prefix, start = false), citations).block()
        }.builder()
    }

    override fun ul(builder: MarkdownDottedList.() -> Unit) = blocked {
        var first = true

        MarkdownDottedList { block ->
            if (!first) writer.newline()
            writer.inline("* ")
            first = false

            MarkdownBuilder(writer.prefixed("  ", start = false), citations).block()
        }.builder()
    }

    override fun cl(builder: MarkdownCheckList.() -> Unit) = blocked {
        var first = true

        MarkdownCheckList { checked, block ->
            if (!first) writer.newline()
            writer.inline(if (checked) "- [x] " else "- [ ] ")
            first = false

            MarkdownBuilder(writer.prefixed("      ", start = false), citations).block()
        }.builder()
    }
}