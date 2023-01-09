package io.koalaql.markout.text

class OnWriteWriter(
    private val of: LineWriter,
    private val action: () -> Unit
): LineWriter {
    private var written = false

    private fun write() {
        if (!written) {
            action()
            written = true
        }
    }

    override fun inline(text: String) {
        write()
        of.inline(text)
    }

    override fun newline() {
        write()
        of.newline()
    }
}