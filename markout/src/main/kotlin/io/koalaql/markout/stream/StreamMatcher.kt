package io.koalaql.markout.stream

import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel

class StreamMatcher(
    private val channel: SeekableByteChannel,
    private val mode: StreamMode = StreamMode.CHECK
): OutputStream() {
    private var buffer = ByteArray(0)

    private var matches = true

    private fun ensureEnoughBuffer(needed: Int) {
        if (buffer.size >= needed) return
        buffer = ByteArray(needed)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        if (matches) {
            ensureEnoughBuffer(len)

            val existing = ByteBuffer.wrap(buffer, 0, len)
            val incoming = ByteBuffer.wrap(b, off, len)

            var read = channel.read(existing)

            if (read < 0) read = 0

            if (read == len) {
                existing.rewind()

                if (existing.mismatch(incoming) == -1) return
            }

            channel.position(channel.position() - read.toLong())

            matches = false
        }

        if (mode == StreamMode.OVERWRITE) {
            channel.write(ByteBuffer.wrap(b, off, len))
        }
    }

    override fun write(byte: Int) {
        write(byteArrayOf(byte.toByte()))
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