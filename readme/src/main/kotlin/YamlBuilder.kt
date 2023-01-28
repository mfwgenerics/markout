import io.koalaql.markout.text.AppendableLineWriter
import io.koalaql.markout.text.LineWriter

interface YamlBuilder {
    operator fun String.minus(other: String)
    operator fun String.minus(block: YamlBuilder.() -> Unit)
    fun li(block:  (YamlBuilder.() -> Unit))

    fun raw(text: String)
}

class YamlBuilderImpl(
    private val lines: LineWriter
): YamlBuilder {
    override fun String.minus(other: String) {
        lines.inline(this)
        lines.inline(": ")
        lines.inline(other)
        lines.newline()
    }

    override fun String.minus(block: YamlBuilder.() -> Unit) {
        lines.inline(this)
        lines.inline(":")
        lines.newline()

        YamlBuilderImpl(lines.prefixed("  ")).block()
    }

    override fun li(block: YamlBuilder.() -> Unit) {
        lines.inline("- ")
        YamlBuilderImpl(lines.prefixed("  ", start = false)).block()
    }

    override fun raw(text: String) {
        lines.raw(text)
        lines.newline()
    }
}

fun yaml(build: YamlBuilder.() -> Unit): String {
    val sb = StringBuilder()

    YamlBuilderImpl(AppendableLineWriter(sb)).build()

    return "$sb"
}