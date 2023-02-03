package io.koalaql.markout.files

import io.koalaql.markout.Diff
import io.koalaql.markout.DiffType
import java.nio.file.Files
import java.nio.file.Path

object DeclareDirectory: FileAction {
    override fun perform(path: Path): Diff? {
        if (Files.isDirectory(path)) return null

        if (Files.deleteIfExists(path)) return Diff(DiffType.MISMATCH, path)

        Files.createDirectory(path)
        return Diff(DiffType.EXPECTED, path)
    }

    override fun expect(path: Path): Diff? {
        if (Files.notExists(path)) {
            return Diff(DiffType.EXPECTED, path)
        }

        if (!Files.isDirectory(path)) {
            return Diff(DiffType.MISMATCH, path)
        }

        return null
    }
}