import io.koalaql.kapshot.Capturable
import io.koalaql.kapshot.Source
import io.koalaql.markout.md.Markdown
import io.koalaql.markout.md.markdownString

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