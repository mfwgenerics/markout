package io.koalaql.markout.files

import io.koalaql.markout.Diff
import io.koalaql.markout.DiffType
import io.koalaql.markout.StreamMatcher
import io.koalaql.markout.output.OutputFile
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.inputStream

data class WriteToFile(
    private val tracked: Boolean,
    private val source: OutputFile
): FileAction {
    override fun perform(path: Path) {
        check (tracked || Files.notExists(path)) {
            "$path already exists"
        }

        source.writeTo(Files.newOutputStream(path))
    }

    override fun expect(path: Path, out: MutableList<Diff>) {
        if (Files.notExists(path)) {
            out.add(Diff(DiffType.EXPECTED, path))
            return
        }

        if (Files.isDirectory(path)) {
            out.add(Diff(DiffType.MISMATCH, path))
            return
        }

        if (!tracked) {
            out.add(Diff(DiffType.UNTRACKED, path))
            return
        }

        val matcher = StreamMatcher(path.inputStream())

        source.writeTo(matcher)

        if (!matcher.matched()) {
            out.add(Diff(DiffType.MISMATCH, path))
            return
        }
    }
}
