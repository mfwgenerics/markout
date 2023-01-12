package io.koalaql.markout.text

class TrimLinesWriter(
    private val of: LineWriter
): LineWriter {
    private var trimLeft = true
    private var saved = arrayListOf<() -> Unit>()

    override fun inline(text: String) {
        if (text.isBlank()) {
            if (trimLeft) return

            saved.add { of.inline(text) }
        } else {
            trimLeft = false

            saved.forEach { it() }
            saved.clear()

            of.inline(text)
        }
    }

    override fun newline() {
        if (trimLeft) return

        saved.add { of.newline() }
    }
}