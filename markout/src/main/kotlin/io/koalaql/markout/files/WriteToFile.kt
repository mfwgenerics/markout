package io.koalaql.markout.files

import io.koalaql.markout.Diff
import io.koalaql.markout.DiffType
import io.koalaql.markout.stream.StreamMatcher
import io.koalaql.markout.output.OutputFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

data class WriteToFile(
    private val source: OutputFile
): FileAction {
    override fun perform(path: Path) {
        source.writeTo(Files.newOutputStream(path))
    }

    override fun expect(path: Path): Diff? {
        if (Files.notExists(path)) return Diff(DiffType.EXPECTED, path)
        if (Files.isDirectory(path)) return Diff(DiffType.MISMATCH, path)

        val matcher = StreamMatcher(Files.newByteChannel(path, StandardOpenOption.READ))

        source.writeTo(matcher)

        if (!matcher.matched()) return Diff(DiffType.MISMATCH, path)

        return null
    }
}
