package io.koalaql.markout.text

class ParagraphWriter(
    private val of: LineWriter
): LineWriter {
    private var startOfLine = true

    override fun inline(text: String) {
        if (!startOfLine) {
            of.inline(text)
            return
        }

        val trimmed = text.trimStart()

        if (trimmed.isEmpty()) return

        of.inline(trimmed)
        startOfLine = false
    }

    override fun newline() {
        if (startOfLine) return
        of.newline()
        startOfLine = true
    }
}