import io.koalaql.kapshot.Capturable
import io.koalaql.kapshot.Source
import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.md.Markdown
import io.koalaql.markout.md.markdown
import io.koalaql.markout.output.Output
import io.koalaql.markout.output.OutputDirectory
import io.koalaql.markout.output.OutputFile
import io.koalaql.markout.text.AppendableLineWriter
import io.koalaql.markout.text.LineWriter

fun interface CapturedBuilderBlock: Capturable<CapturedBuilderBlock> {
    fun Markdown.build()

    override fun withSource(source: Source): CapturedBuilderBlock = object : CapturedBuilderBlock by this {
        override val source = source
    }
}

fun Markdown.example(block: CapturedBuilderBlock) {
    h3("Kotlin")
    code("kotlin", block.source.text)

    val rendered = markdown { with(block) { build() } }

    h3("Generated")
    code("markdown", rendered)

    h3("Rendered")
    quote {
        with (block) { build() }
    }
}

@MarkoutDsl
fun interface Sections {
    @MarkoutDsl
    fun section(title: String, contents: Markdown.() -> Unit)
}

@MarkoutDsl
fun Markdown.sectioned(builder: Sections.() -> Unit) {
    val actions = arrayListOf<Markdown.() -> Unit>()

    ol {
        builder { title, contents ->
            li { a("#${title.lowercase().replace(' ', '-')}", title) }

            actions.add { h2(title) }
            actions.add(contents)
        }
    }

    actions.forEach { it() }
}

private class PrefixPair(
    val indent: String = "",
    val before: String = "",
)

private class Prefix(
    val pre: PrefixPair = PrefixPair(),
    val post: PrefixPair = PrefixPair(),
)

private val NO_PREFIX = Prefix(
    PrefixPair("", ""),
    PrefixPair("", "")
)

private val PIPES_PREFIX = Prefix(
    PrefixPair("│  ", "├─ "),
    PrefixPair("   ", "└─ ")
)

private fun drawFileTree(
    prefix: Prefix,
    output: Output,
    writer: LineWriter
) {
    when (output) {
        is OutputDirectory -> {
            val entries = (mapOf(".markout" to OutputFile { }) + output.entries())
                .entries
                .toList()

            entries.forEachIndexed { ix, (key, output) ->
                val p = if (ix < entries.size - 1) prefix.pre else prefix.post

                writer.inline(p.before)
                writer.inline(key)
                writer.newline()

                drawFileTree(PIPES_PREFIX, output, writer.prefixed(p.indent))
            }
        }
        is OutputFile -> { }
    }
}

fun drawFileTree(output: Output): String =
    "${StringBuilder().also { drawFileTree(NO_PREFIX, output, AppendableLineWriter(it).trimmedLines()) }}"
