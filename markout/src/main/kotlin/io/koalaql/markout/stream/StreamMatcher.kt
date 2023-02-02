package io.koalaql.markout.stream

import java.io.InputStream
import java.io.OutputStream

class StreamMatcher(
    private val input: InputStream
): OutputStream() {
    private var matches = true

    fun matched(): Boolean {
        return matches && input.read() == -1
    }

    override fun write(byte: Int) {
        matches = matches && input.read() == byte and 0xFF
    }
}