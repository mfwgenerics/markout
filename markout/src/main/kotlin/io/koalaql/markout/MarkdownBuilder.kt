package io.koalaql.markout

import io.koalaql.markout.md.*

class MarkdownBuilder(
    private var state: BuilderState = BuilderState.FRESH,
    private val append: (String) -> Unit
) : Markdown {
    enum class BuilderState {
        FRESH,
        INLINE,
        AFTER_BLOCK
    }

    override fun t(line: MarkdownInline.() -> Unit) {
        if (state == BuilderState.AFTER_BLOCK) append("\n\n")

        state = BuilderState.INLINE

        this.line()
    }

    override fun t(text: String) =
        t { this@MarkdownBuilder.append(text) }

    override fun c(text: String) = t {
        this@MarkdownBuilder.append("`$text`")
    }

    override fun i(block: MarkdownInline.() -> Unit) = t {
        this@MarkdownBuilder.append("*")
        this.block()
        this@MarkdownBuilder.append("*")
    }

    override fun b(block: MarkdownInline.() -> Unit) = t {
        this@MarkdownBuilder.append("**")
        this.block()
        this@MarkdownBuilder.append("**")
    }

    override fun p(block: MarkdownBlock.() -> Unit) {
        if (state == BuilderState.AFTER_BLOCK || state == BuilderState.INLINE) {
            this@MarkdownBuilder.append("\n\n")
        }

        MarkdownBuilder(BuilderState.FRESH, append).block()

        state = BuilderState.AFTER_BLOCK
    }

    override fun h1(block: MarkdownInline.() -> Unit) = p {
        this@MarkdownBuilder.append("# ")
        this.block()
    }

    override fun h2(block: MarkdownInline.() -> Unit) = p {
        this@MarkdownBuilder.append("## ")
        this.block()
    }

    override fun h3(block: MarkdownInline.() -> Unit) = p {
        this@MarkdownBuilder.append("### ")
        this.block()
    }

    override fun quote(block: Markdown.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun code(code: String) = p {
        this@MarkdownBuilder.append("```\n")
        this@MarkdownBuilder.append(code)
        this@MarkdownBuilder.append("\n```")
    }

    override fun ol(builder: MarkdownList.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun ul(builder: MarkdownList.() -> Unit) {
        TODO("Not yet implemented")
    }
}