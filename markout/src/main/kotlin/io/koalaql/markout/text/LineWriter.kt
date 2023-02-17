package io.koalaql.markout.text

interface LineWriter {
    fun prefixed(prefix: String, start: Boolean = true): LineWriter =
        PrefixedLineWriter(this, prefix, start)

    fun onWrite(action: () -> Unit): LineWriter =
        OnWriteWriter(this, action)

    fun trimmedLines(): LineWriter =
        TrimLinesWriter(this)

    fun paragraphRules(): LineWriter =
        ParagraphWriter(this)

    fun inline(text: String)
    fun newline()

    fun line(text: String) {
        inline(text)
        newline()
    }

    fun line() = newline()

    fun raw(text: String) {
        if (text.isEmpty()) return

        val iter = text.splitToSequence("\n").iterator()

        inline(iter.next()) /* guaranteed next due to non-empty text */

        /* remaining lines */
        iter.forEach {
            newline()
            inline(it)
        }
    }
}