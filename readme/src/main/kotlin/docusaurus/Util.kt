package docusaurus

import io.koalaql.kapshot.CapturedBlock
import io.koalaql.markout.docusaurus.DocusaurusMarkdown
import io.koalaql.markout.md.Markdown
import io.koalaql.markout.md.markdown
import io.koalaql.markout.text.AppendableLineWriter

fun execBlock(block: CapturedBlock<Unit>): String =
    block.source.text.also { block.invoke() }

fun DocusaurusMarkdown.tabbed(
    imports: Boolean,
    tabs: Map<String, Markdown.() -> Unit>
) {
    val builder = StringBuilder()

    AppendableLineWriter(builder).apply {
        if (imports) {
            line("import Tabs from '@theme/Tabs';")
            line("import TabItem from '@theme/TabItem';")
            line()
        }

        line("<Tabs>")
        tabs.forEach { (k, v) ->
            line("<TabItem value='${k.lowercase()}' label='${k}'>")
            line()
            raw(markdown(v))
            line()
            line()
            line("</TabItem>")
        }
        line("</Tabs>")
    }

    code("mdx-code-block", "$builder")
}