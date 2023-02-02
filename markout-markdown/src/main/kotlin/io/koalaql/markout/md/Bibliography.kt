package io.koalaql.markout.md

class Bibliography {
    private val foots = arrayListOf<Pair<String, Markdown.() -> Unit>>()
    private val refs = linkedMapOf<String, Citation>()

    fun reference(reference: String): Citation =
        refs.getOrPut(reference) { Citation("[${refs.size + 1}]") }

    fun footnote(builder: Markdown.() -> Unit): String {
        val label = "[^${foots.size + 1}]"

        foots.add(Pair(label, builder))

        return label
    }

    val footnotes: List<Pair<String, Markdown.() -> Unit>> = foots
    val references: Map<String, Citation> = refs
}