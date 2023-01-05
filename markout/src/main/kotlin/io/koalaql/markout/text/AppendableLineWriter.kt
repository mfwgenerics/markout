package io.koalaql.markout.text

class AppendableLineWriter(
    private val appendable: Appendable
): LineWriter {
    override fun inline(text: String) { appendable.append(text) }
    override fun newline() { appendable.append("\n") }
    override fun raw(text: String) { appendable.append(text) }
}