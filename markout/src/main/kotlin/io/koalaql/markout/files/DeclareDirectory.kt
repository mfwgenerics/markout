package io.koalaql.markout.files

import io.koalaql.markout.Diff
import java.nio.file.Files
import java.nio.file.Path

data class DeclareDirectory(
    private val delete: Boolean,
): FileAction {
    override fun perform(path: Path) {
        DeleteFile.perform(path)
        if (!Files.isDirectory(path)) Files.createDirectory(path)
    }

    override fun expect(path: Path, out: MutableList<Diff>) { }
}