import io.koalaql.kapshot.Capturable
import io.koalaql.kapshot.Source
import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.buildOutput
import io.koalaql.markout.md.Markdown
import io.koalaql.markout.md.markdownString
import io.koalaql.markout.output.Output
import io.koalaql.markout.output.OutputDirectory
import io.koalaql.markout.output.OutputFile

fun interface CapturedBuilderBlock: Capturable<CapturedBuilderBlock> {
    fun Markdown.build()

    override fun withSource(source: Source): CapturedBuilderBlock = object : CapturedBuilderBlock by this {
        override val source = source
    }
}

fun Markdown.example(block: CapturedBuilderBlock) {
    h3("Kotlin")
    code("kotlin", block.source.text)

    val rendered = markdownString { with(block) { build() } }

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

private fun pipeify(indent: String) = Prefix(
    PrefixPair("$indent│  ", "$indent├─ "),
    PrefixPair("$indent   ", "$indent└─ ")
)

private class PrefixPair(
    val indent: String = "",
    val before: String = "",
)

private class Prefix(
    val pre: PrefixPair = PrefixPair(),
    val post: PrefixPair = PrefixPair(),
)

private fun drawFileTree(
    prefix: Prefix,
    output: Output,
    sb: StringBuilder
) {
    when (output) {
        is OutputDirectory -> {
            val entries = output.entries().entries.toList()

            entries.forEachIndexed { ix, (key, output) ->
                val p = if (ix < entries.size - 1) prefix.pre else prefix.post

                sb.append("\n${p.before}")
                sb.append(key)

                drawFileTree(pipeify(p.indent), output, sb)
            }
        }
        is OutputFile -> { }
    }
}

fun drawFileTree(output: Output): String =
    "${StringBuilder().also { drawFileTree(Prefix(), output, it) }}"

fun main() {
    println(drawFileTree(buildOutput {
        directory("dir") {
            file("file1", "")

            directory("dir2") {
                file("file2", "")
                file("file3", "")

                directory("dir4") {
                    file("file2", "")
                    file("file3", "")
                }

                directory("dir6") {
                    file("file2", "")
                    file("file3", "")
                }
            }

            file("file4", "")

            directory("dir3") {
                file("file2", "")
                file("file3", "")
            }
        }

        file("file", "")
    }))
}