package io.koalaql.markout.stream

import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel

class StreamMatcher(
    private val channel: SeekableByteChannel,
    private val mode: StreamMode = StreamMode.CHECK
): OutputStream() {
    private var matches = true

    private fun read(): Int {
        val b = ByteArray(1)

        val count = channel.read(ByteBuffer.wrap(b))

        if (count == 1) return b[0].toInt() and 0xFF

        return -1
    }

    override fun write(byte: Int) {
        matches = matches && read() == byte and 0xFF
    }

    override fun close() {
        if (channel.size() != channel.position()) {
            if (mode == StreamMode.OVERWRITE) channel.truncate(channel.position())
            matches = false
        }

        channel.close()
    }

    fun matched(): Boolean {
        return matches
    }
}