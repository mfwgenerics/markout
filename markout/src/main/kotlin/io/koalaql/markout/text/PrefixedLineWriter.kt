package io.koalaql.markout.text

class PrefixedLineWriter(
    private val of: LineWriter,
    private val prefix: String,
    private var start: Boolean = true
): LineWriter {
    private fun emit() {
        if (start) {
            of.inline(prefix)
            start = false
        }
    }

    override fun inline(text: String) {
        if (text.isEmpty()) return

        emit()
        of.inline(text)
    }

    override fun newline() {
        emit()
        of.newline()
        start = true
    }
}