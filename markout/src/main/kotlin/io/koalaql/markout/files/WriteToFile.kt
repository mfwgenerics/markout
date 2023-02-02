package io.koalaql.markout.files

import io.koalaql.markout.Diff
import io.koalaql.markout.DiffType
import io.koalaql.markout.stream.StreamMatcher
import io.koalaql.markout.output.OutputFile
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.inputStream

data class WriteToFile(
    private val source: OutputFile
): FileAction {
    override fun perform(path: Path) {
        source.writeTo(Files.newOutputStream(path))
    }

    override fun expect(path: Path, out: MutableList<Diff>): Boolean {
        if (Files.notExists(path)) {
            out.add(Diff(DiffType.EXPECTED, path))
            return false
        }

        if (Files.isDirectory(path)) {
            out.add(Diff(DiffType.MISMATCH, path))
            return false
        }

        val matcher = StreamMatcher(Files.newByteChannel(path, StandardOpenOption.READ))

        source.writeTo(matcher)

        if (!matcher.matched()) {
            out.add(Diff(DiffType.MISMATCH, path))
            return false
        }

        return true
    }
}
