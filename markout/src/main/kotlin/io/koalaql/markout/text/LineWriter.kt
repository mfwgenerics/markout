package io.koalaql.markout.text

interface LineWriter {
    fun prefixed(prefix: String): LineWriter =
        PrefixedLineWriter(this, prefix)

    fun inline(text: String)
    fun newline()

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