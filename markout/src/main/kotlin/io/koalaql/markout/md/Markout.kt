package io.koalaql.markout.md

import io.koalaql.markout.Markout

fun Markout.markdown(name: String, builder: Markdown.() -> Unit) {
    val sb = StringBuilder()

    object : Markdown {
        override fun t(text: String) {
            sb.append(text)
        }

        override fun t(line: MarkdownInline.() -> Unit) {
            TODO("Not yet implemented")
        }

        override fun i(block: Markdown.() -> Unit) {
            sb.append("*")
            this.block()
            sb.append("*")
        }

        override fun b(block: Markdown.() -> Unit) {
            sb.append("**")
            this.block()
            sb.append("**")
        }

        override fun h1(block: MarkdownInline.() -> Unit) {
            sb.append("#")
            this.block()
        }

        override fun h2(block: MarkdownInline.() -> Unit) {
            sb.append("##")
            this.block()
        }

        override fun h3(block: MarkdownInline.() -> Unit) {
            sb.append("###")
            this.block()
        }

        override fun p(block: MarkdownBlock.() -> Unit) {
            TODO("Not yet implemented")
        }

        override fun quote(block: Markdown.() -> Unit) {
            TODO("Not yet implemented")
        }

        override fun code(code: String) {
            TODO("Not yet implemented")
        }

        override fun ol(builder: MarkdownList.() -> Unit) {
            TODO("Not yet implemented")
        }

        override fun ul(builder: MarkdownList.() -> Unit) {
            TODO("Not yet implemented")
        }
    }.builder()

    file("$name.md", "$sb")
}