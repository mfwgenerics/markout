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

    private fun read(): Int {
        val b = ByteArray(1)

        val count = channel.read(ByteBuffer.wrap(b))

        if (count == 1) return b[0].toInt() and 0xFF

        return -1
    }

    override fun write(byte: Int) {
        if (matches) {
            val position = channel.position()

            matches = read() == byte and 0xFF

            if (!matches) channel.position(position)
        }

        if (!matches && mode == StreamMode.OVERWRITE) {
            channel.write(ByteBuffer.wrap(byteArrayOf(byte.toByte())))
        }
    }

    private fun ensureEnoughBuffer(needed: Int) {
        if (buffer.size >= needed) return

        buffer = ByteArray(needed)
    }

    /*override fun write(b: ByteArray, off: Int, len: Int) {
        ensureEnoughBuffer(len)
    }*/

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