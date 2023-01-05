package io.koalaql.markout.text

class PrefixedLineWriter(
    private val of: LineWriter,
    private val prefix: String
): LineWriter {
    private var sol = true

    private fun emit() {
        if (sol) {
            of.inline(prefix)
            sol = false
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
        sol = true
    }
}