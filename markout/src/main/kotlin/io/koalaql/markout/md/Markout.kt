package io.koalaql.markout.md

import io.koalaql.markout.Markout
import io.koalaql.markout.MarkoutDsl

private enum class BuilderState {
    FRESH,
    INLINE,
    AFTER_BLOCK
}

@MarkoutDsl
fun Markout.markdown(name: String, builder: Markdown.() -> Unit) {
    val sb = StringBuilder()

    class MarkdownImpl(
        var state: BuilderState = BuilderState.FRESH
    ) : Markdown {
        override fun t(line: MarkdownInline.() -> Unit) {
            if (state == BuilderState.AFTER_BLOCK) sb.append("\n\n")

            state = BuilderState.INLINE

            this.line()
        }

        override fun t(text: String) =
            t { sb.append(text) }

        override fun i(block: MarkdownInline.() -> Unit) = t {
            sb.append("*")
            this.block()
            sb.append("*")
        }

        override fun b(block: MarkdownInline.() -> Unit) = t {
            sb.append("**")
            this.block()
            sb.append("**")
        }

        override fun p(block: MarkdownBlock.() -> Unit) {
            if (state == BuilderState.AFTER_BLOCK || state == BuilderState.INLINE) {
                sb.append("\n\n")
            }

            MarkdownImpl(BuilderState.FRESH).block()

            state = BuilderState.AFTER_BLOCK
        }

        override fun h1(block: MarkdownInline.() -> Unit) = p {
            sb.append("# ")
            this.block()
        }

        override fun h2(block: MarkdownInline.() -> Unit) = p {
            sb.append("## ")
            this.block()
        }

        override fun h3(block: MarkdownInline.() -> Unit) = p {
            sb.append("### ")
            this.block()
        }

        override fun quote(block: Markdown.() -> Unit) {
            TODO("Not yet implemented")
        }

        override fun code(code: String) = p {
            sb.append("```\n")
            sb.append(code)
            sb.append("\n```")
        }

        override fun ol(builder: MarkdownList.() -> Unit) {
            TODO("Not yet implemented")
        }

        override fun ul(builder: MarkdownList.() -> Unit) {
            TODO("Not yet implemented")
        }
    }

    MarkdownImpl(BuilderState.FRESH).builder()

    file("$name.md", "$sb")
}