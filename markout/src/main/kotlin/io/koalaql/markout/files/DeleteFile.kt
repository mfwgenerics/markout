package io.koalaql.markout.files

import io.koalaql.markout.Diff
import io.koalaql.markout.DiffType
import java.nio.file.Files
import java.nio.file.Path

object DeleteFile: FileAction {
    private fun isEmpty(dir: Path) =
        Files.newDirectoryStream(dir).use { directory -> !directory.iterator().hasNext() }

    override fun perform(path: Path): Diff? {
        if (Files.isDirectory(path)) {
            if (isEmpty(path)) {
                Files.delete(path)

                return Diff(DiffType.UNEXPECTED, path)
            }
        } else {
            if (Files.deleteIfExists(path)) return Diff(
                DiffType.UNEXPECTED,
                path
            )
        }

        return null
    }

    override fun expect(path: Path): Diff? {
        if (Files.notExists(path)) return null

        return Diff(
            DiffType.UNEXPECTED,
            path
        )
    }
}