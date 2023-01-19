package io.koalaql.markout.files

import io.koalaql.markout.Diff
import io.koalaql.markout.DiffType
import java.nio.file.Files
import java.nio.file.Path

object DeleteFile: FileAction {
    private fun isEmpty(dir: Path) =
        Files.newDirectoryStream(dir).use { directory -> !directory.iterator().hasNext() }

    override fun perform(path: Path) {
        if (Files.isDirectory(path)) {
            if (isEmpty(path)) Files.delete(path)
        } else {
            Files.deleteIfExists(path)
        }
    }

    override fun expect(path: Path, out: MutableList<Diff>): Boolean {
        if (Files.exists(path)) {
            out.add(Diff(
                DiffType.UNEXPECTED,
                path
            ))

            return false
        }

        return true
    }
}